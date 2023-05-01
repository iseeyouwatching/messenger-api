package ru.hits.messengerapi.friends.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.hits.messengerapi.common.exception.ConflictException;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.common.helpingservices.implementation.CheckPaginationInfoService;
import ru.hits.messengerapi.common.security.JwtUserData;
import ru.hits.messengerapi.friends.dto.blacklist.*;
import ru.hits.messengerapi.friends.dto.common.AddPersonDto;
import ru.hits.messengerapi.friends.dto.common.PaginationWithFullNameFilterDto;
import ru.hits.messengerapi.friends.entity.BlacklistEntity;
import ru.hits.messengerapi.friends.entity.FriendEntity;
import ru.hits.messengerapi.friends.repository.BlacklistRepository;
import ru.hits.messengerapi.friends.repository.FriendsRepository;
import ru.hits.messengerapi.friends.service.BlacklistServiceInterface;

import java.time.LocalDate;
import java.util.*;

/**
 *  Сервис черного списка.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BlacklistService implements BlacklistServiceInterface {

    /**
     * Репозиторий для работы с сущностью {@link BlacklistEntity}.
     */
    private final BlacklistRepository blacklistRepository;

    /**
     * Вспомогательный сервис для проверки данных для пагинации.
     */
    private final CheckPaginationInfoService checkPaginationInfoService;

    /**
     * Сервис, в котором хранится логика отправки интеграционных запросов.
     */
    private final IntegrationRequestsService integrationRequestsService;

    /**
     * Сервис друзей.
     */
    private final FriendsService friendsService;

    /**
     * Репозиторий для работы с сущностью {@link FriendEntity}.
     */
    private final FriendsRepository friendsRepository;

    /**
     * Получить заблокированных пользователей с информацией о странице и фильтре по ФИО для целевого пользователя.
     *
     * @param paginationWithFullNameFilterDto объект {@link PaginationWithFullNameFilterDto}, содержащий информацию о странице и фильтре по ФИО.
     * @return список заблокированных пользователей, информация о странице и фильтре по ФИО.
     */
    @Override
    public BlockedUsersPageListDto getBlockedUsers(PaginationWithFullNameFilterDto paginationWithFullNameFilterDto) {
        int pageNumber = paginationWithFullNameFilterDto.getPageInfo().getPageNumber();
        int pageSize = paginationWithFullNameFilterDto.getPageInfo().getPageSize();
        checkPaginationInfoService.checkPagination(pageNumber, pageSize);
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        UUID targetUserId = getAuthenticatedUserId();

        List<BlacklistEntity> blockedUsers;
        if (paginationWithFullNameFilterDto.getFullNameFilter() == null ||
                paginationWithFullNameFilterDto.getFullNameFilter().isBlank()) {
            blockedUsers = blacklistRepository
                    .findAllByTargetUserIdAndDeletedDate(targetUserId, null, pageable);
        }
        else {
            String wildcardFullNameFilter = "%" + paginationWithFullNameFilterDto.getFullNameFilter() + "%";
            blockedUsers = blacklistRepository.findAllByTargetUserIdAndBlockedUserNameLikeIgnoreCaseAndDeletedDate(
                    targetUserId, wildcardFullNameFilter, null, pageable);
        }

        List<BlockedUserInfoDto> blockedUserInfoDtos = new ArrayList<>();
        for (BlacklistEntity blockedUser : blockedUsers) {
            blockedUserInfoDtos.add(new BlockedUserInfoDto(blockedUser));
        }

        log.info("Метод на получение заблокированных пользователей успешно выполнен с параметрами " +
                "pageNumber={}, pageSize={}, targetUserId={}, fullNameFilter={}",
                pageNumber, pageSize, targetUserId, paginationWithFullNameFilterDto.getFullNameFilter());

        return new BlockedUsersPageListDto(
                blockedUserInfoDtos,
                paginationWithFullNameFilterDto.getPageInfo(),
                paginationWithFullNameFilterDto.getFullNameFilter()
        );
    }

    /**
     * Получить информацию о заблокированном пользователе.
     *
     * @param blockedUserId id заблокированного пользователя.
     * @return полная информация о заблокированном пользователе.
     * @throws NotFoundException если пользователя нет в ЧС.
     */
    @Override
    public BlockedUserDto getBlockedUser(UUID blockedUserId) {
        UUID targetUserId = getAuthenticatedUserId();

        Optional<BlacklistEntity> blockedUser = blacklistRepository.findByTargetUserIdAndBlockedUserId(
                targetUserId,
                blockedUserId
        );

        if (blockedUser.isEmpty() || blockedUser.get().getDeletedDate() != null) {
            String message = "Пользователя с ID " + blockedUserId
                    + " нет в черном списке у пользователя с ID " + targetUserId + ".";
            log.error(message);
            throw new NotFoundException(message);
        }

        String successMessage = "Пользователь с ID {} успешно найден в черном списке у пользователя с ID {}.";
        log.info(successMessage, blockedUserId, targetUserId);

        return new BlockedUserDto(blockedUser.get());
    }

    /**
     * Добавить пользователя в черный список.
     *
     * @param addPersonDto DTO, содержащая информацию о добавляемом в черный список пользователе
     * @return полная информация о заблокированном пользователе
     * @throws ConflictException если 1) пользователь хочет добавить самого себя в ЧС;
     *                                2) целевой пользователь хочет добавить в ЧС пользователя,
     *                                который уже добавлен в ЧС
     */
    @Override
    public BlockedUserDto addToBlacklist(AddPersonDto addPersonDto) {
        integrationRequestsService.checkUserExistence(addPersonDto);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
        UUID targetUserId = userData.getId();

        Optional<BlacklistEntity> blockedUser = blacklistRepository.findByTargetUserIdAndBlockedUserId(
                targetUserId, addPersonDto.getId());

        log.info("Пользователь с ID {} и ФИО {} хочет добавить пользователя с ID {} и ФИО {} в черный список.",
                targetUserId, userData.getFullName(), addPersonDto.getId(), addPersonDto.getFullName());

        if (addPersonDto.getId().equals(targetUserId)) {
            log.warn("Пользователь с ID {} и ФИО {} пытается добавить самого себя в черный список.",
                    targetUserId, userData.getFullName());
            throw new ConflictException("Пользователь не может добавить самого себя в черный список.");
        }

        if (blockedUser.isPresent() && blockedUser.get().getDeletedDate() == null) {
            log.warn("Пользователь с ID {} и ФИО {} уже добавлен в черный список пользователя с ID {} и ФИО {}.",
                    addPersonDto.getId(), addPersonDto.getFullName(), targetUserId, userData.getFullName());
            throw new ConflictException("Пользователь с ID " + addPersonDto.getId() + " и ФИО "
                    + addPersonDto.getFullName() + " уже добавлен в черный список.");
        }

        Optional<FriendEntity> friend = friendsRepository.findByTargetUserIdAndAddedUserId(
                targetUserId,
                addPersonDto.getId());

        if (friend.isPresent() && (friend.get().getDeletedDate() == null)) {
            log.info("Удаляем пользователя с ID {} и ФИО {} из друзей пользователя с ID {} и ФИО {}, " +
                            "так как он добавлен в черный список.",
                    addPersonDto.getId(), addPersonDto.getFullName(), targetUserId, userData.getFullName());
            friendsService.deleteFriend(addPersonDto.getId());
            integrationRequestsService.syncFriendData(addPersonDto.getId());
        }

        if (blockedUser.isPresent()) {
            blockedUser.get().setDeletedDate(null);
            blockedUser.get().setIsDeleted(false);
            blockedUser.get().setAddedDate(LocalDate.now());
            integrationRequestsService.syncBlockedUserData(addPersonDto.getId());
            blacklistRepository.save(blockedUser.get());
            log.info("Пользователь с ID {} и ФИО {} уже существует в черном списке пользователя " +
                            "с ID {} и ФИО {}, поэтому мы его только обновляем.",
                    addPersonDto.getId(), addPersonDto.getFullName(), targetUserId, userData.getFullName());
            return new BlockedUserDto(blockedUser.get());
        }

        BlacklistEntity newBlockedUser = new BlacklistEntity();
        newBlockedUser.setAddedDate(LocalDate.now());
        newBlockedUser.setIsDeleted(false);
        newBlockedUser.setTargetUserId(targetUserId);
        newBlockedUser.setBlockedUserId(addPersonDto.getId());
        newBlockedUser.setBlockedUserName(addPersonDto.getFullName());
        newBlockedUser = blacklistRepository.save(newBlockedUser);
        log.info("Пользователь с ID {} и ФИО {} добавлен в черный список пользователя с ID {} и ФИО {}.",
                addPersonDto.getId(), addPersonDto.getFullName(), targetUserId, userData.getFullName());

        return new BlockedUserDto(newBlockedUser);
    }

    /**
     * Удалить пользователя из черного списка.
     *
     * @param blockedUserId id заблокированного пользователя.
     * @return полная информация о заблокированном пользователе.
     * @throws NotFoundException если пользователя нет в ЧС.
     * @throws ConflictException если пользователь уже удален из ЧС.
     */
    @Override
    public BlockedUserDto deleteFromBlacklist(UUID blockedUserId) {
        UUID targetUserId = getAuthenticatedUserId();

        Optional<BlacklistEntity> blockedUser = blacklistRepository.findByTargetUserIdAndBlockedUserId(
                targetUserId,
                blockedUserId
        );

        if (blockedUser.isEmpty()) {
            log.warn("Пользователя с ID {} нет в черном списке у пользователя с ID {}.",
                    blockedUserId, targetUserId);
            throw new NotFoundException("Пользователя с ID " + blockedUserId
                    + " нет в черном списке у пользователя с ID " + targetUserId + ".");
        }

        if (blockedUser.get().getDeletedDate() == null) {
            blockedUser.get().setDeletedDate(LocalDate.now());
            blockedUser.get().setIsDeleted(true);
            blacklistRepository.save(blockedUser.get());
            log.info("Пользователь с ID {} успешно удален из черного списка у пользователя с ID {}.",
                    blockedUserId, targetUserId);
        }
        else {
            log.warn("Пользователь с ID {} уже удален из черного списка у пользователя с ID {}.",
                    blockedUserId, targetUserId);
            throw new ConflictException("Пользователь с ID " + blockedUserId
                    + " уже удален из черного списка у пользователя с ID " + targetUserId + ".");
        }

        return new BlockedUserDto(blockedUser.get());
    }

    /**
     * Поиск среди заблокированных пользователей.
     *
     * @param paginationAndFilters информация о пагинации и фильтрах.
     * @return найденные заблокированные пользователи с информацией о странице и фильтрах.
     */
    @Override
    public SearchedBlockedUsersDto searchBlockedUsers(PaginationWithBlockedUserFiltersDto paginationAndFilters) {
        int pageNumber = paginationAndFilters.getPageInfo().getPageNumber();
        int pageSize = paginationAndFilters.getPageInfo().getPageSize();
        checkPaginationInfoService.checkPagination(pageNumber, pageSize);
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        UUID targetUserId = getAuthenticatedUserId();

        Page<BlacklistEntity> pageBlockedUsers;
        Example<BlacklistEntity> example;
        if (paginationAndFilters.getFilters() != null) {
            example = Example.of(BlacklistEntity
                    .builder()
                    .addedDate(paginationAndFilters.getFilters().getAddedDate())
                    .blockedUserId(paginationAndFilters.getFilters().getBlockedUserId())
                    .blockedUserName(paginationAndFilters.getFilters().getBlockedUserName())
                    .targetUserId(targetUserId)
                    .isDeleted(false)
                    .build());
        } else {
            example = Example.of(BlacklistEntity
                    .builder()
                    .targetUserId(targetUserId)
                    .isDeleted(false)
                    .build());
        }
        pageBlockedUsers = blacklistRepository.findAll(example, pageable);

        List<BlacklistEntity> blockedUsers = pageBlockedUsers.getContent();
        List<BlockedUserInfoDto> blockedUserInfoDtos = new ArrayList<>();

        for (BlacklistEntity blockedUser: blockedUsers) {
            blockedUserInfoDtos.add(new BlockedUserInfoDto(blockedUser));
        }

        SearchedBlockedUsersDto searchedBlockedUsersDto = new SearchedBlockedUsersDto();
        searchedBlockedUsersDto.setBlockedUsers(blockedUserInfoDtos);
        searchedBlockedUsersDto.setFilters(paginationAndFilters.getFilters());
        searchedBlockedUsersDto.setPageInfo(paginationAndFilters.getPageInfo());

        log.info("Выполнен поиск заблокированных пользователей у пользователя с ID {}.",
                targetUserId);

        return searchedBlockedUsersDto;
    }

    /**
     * Проверка нахождения пользователя в черном списке.
     *
     * @param blockedUserId id заблокированного пользователя
     * @return true - если пользователь находится в ЧС, false - если нет.
     */
    @Override
    public boolean checkIfTheUserBlacklisted(UUID blockedUserId) {
        UUID targetUserId = getAuthenticatedUserId();

        Optional<BlacklistEntity> blockedUser = blacklistRepository.findByTargetUserIdAndBlockedUserId(
                targetUserId,
                blockedUserId
        );

        boolean isBlocked = blockedUser.isPresent() && blockedUser.get().getDeletedDate() == null;

        if (isBlocked) {
            log.info("Пользователь с ID {} находится в черном списке у пользователя с ID {}",
                    blockedUserId, targetUserId);
        } else {
            log.info("Пользователь с ID {} не находится в черном списке у пользователя с ID {}",
                    blockedUserId, targetUserId);
        }

        return isBlocked;
    }

    /**
     * Проверка нахождения целевого пользователя в черном списке у добавляемого в друзья пользователе.
     *
     * @param targetUserId id целевого пользователя.
     * @param blockedUserId id заблокированного пользователя.
     * @return true - если пользователь находится в ЧС, false - если нет.
     */
    @Override
    public boolean checkIfTheTargetUserBlacklisted(UUID targetUserId, UUID blockedUserId) {
        Optional<BlacklistEntity> blockedUser = blacklistRepository.findByTargetUserIdAndBlockedUserId(
                targetUserId,
                blockedUserId
        );

        boolean isBlocked = blockedUser.isPresent() && blockedUser.get().getDeletedDate() == null;
        if (isBlocked) {
            log.info("Пользователь с ID {} заблокирован пользователем с ID {}", blockedUserId, targetUserId);
        } else {
            log.info("Пользователь с ID {} не заблокирован пользователем с ID {}", blockedUserId, targetUserId);
        }

        return isBlocked;
    }

    /**
     * Метод для получения ID аутентифицированного пользователя.
     *
     * @return ID аутентифицированного пользователя.
     */
    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
        return userData.getId();
    }


}
