package ru.hits.messengerapi.friends.service.implementation;

import lombok.RequiredArgsConstructor;
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

import java.time.LocalDateTime;
import java.util.*;

/**
 *  Сервис черного списка.
 */
@Service
@RequiredArgsConstructor
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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
        UUID targetUserId = userData.getId();

        List<BlacklistEntity> blockedUsers;
        if (paginationWithFullNameFilterDto.getFullNameFilter() == null || paginationWithFullNameFilterDto.getFullNameFilter().isBlank()) {
            blockedUsers = blacklistRepository
                    .findAllByTargetUserIdAndDeletedDate(targetUserId, null, pageable);
        }
        else {
            String wildcardFullNameFilter = "%" + paginationWithFullNameFilterDto.getFullNameFilter() + "%";
            blockedUsers = blacklistRepository.findAllByTargetUserIdAndBlockedUserNameLikeAndDeletedDate(
                    targetUserId, wildcardFullNameFilter, null, pageable);
        }

        List<BlockedUserInfoDto> blockedUserInfoDtos = new ArrayList<>();
        for (BlacklistEntity blockedUser : blockedUsers) {
            blockedUserInfoDtos.add(new BlockedUserInfoDto(blockedUser));
        }

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
        UUID targetUserId = userData.getId();

        Optional<BlacklistEntity> blockedUser = blacklistRepository.findByTargetUserIdAndBlockedUserId(
                targetUserId,
                blockedUserId
        );

        if (blockedUser.isEmpty() || blockedUser.get().getDeletedDate() != null) {
            throw new NotFoundException("Пользователя с ID " + blockedUserId
                    + " нет в черном списке у пользователя с ID " + targetUserId + ".");
        }

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

        if (addPersonDto.getId().equals(targetUserId)) {
            throw new ConflictException("Пользователь не может добавить самого себя в черный список.");
        }

        Optional<BlacklistEntity> blockedUser = blacklistRepository.findByTargetUserIdAndBlockedUserId(
                targetUserId, addPersonDto.getId());

        if (blockedUser.isPresent() && blockedUser.get().getDeletedDate() == null) {
            throw new ConflictException("Пользователь с ID " + addPersonDto.getId() + " и ФИО "
                    + addPersonDto.getFullName() + " уже добавлен в черный список.");
        }

        Optional<FriendEntity> friend = friendsRepository.findByTargetUserIdAndAddedUserId(
                targetUserId,
                addPersonDto.getId());

        if (friend.isPresent() && (friend.get().getDeletedDate() == null)) {
            friendsService.deleteFriend(addPersonDto.getId());
            friendsService.syncFriendData(addPersonDto.getId());
        }

        if (blockedUser.isPresent()) {
            blockedUser.get().setDeletedDate(null);
            blockedUser.get().setAddedDate(LocalDateTime.now());
            syncBlockedUserData(addPersonDto.getId());
            blacklistRepository.save(blockedUser.get());
            return new BlockedUserDto(blockedUser.get());
        }

        BlacklistEntity newBlockedUser = new BlacklistEntity();
        newBlockedUser.setAddedDate(LocalDateTime.now());
        newBlockedUser.setTargetUserId(targetUserId);
        newBlockedUser.setBlockedUserId(addPersonDto.getId());
        newBlockedUser.setBlockedUserName(addPersonDto.getFullName());
        newBlockedUser = blacklistRepository.save(newBlockedUser);

        return new BlockedUserDto(newBlockedUser);
    }

    /**
     * Синхронизация данных заблокированного пользователя.
     *
     * @param id идентификатор пользователя.
     * @return сообщение об успешной синхронизации.
     */
    @Override
    public Map<String, String> syncBlockedUserData(UUID id) {
        String fullName = integrationRequestsService.getFullName(id);

        List<BlacklistEntity> blockedUsers = blacklistRepository.findAllByBlockedUserId(id);

        for (BlacklistEntity blockedUser: blockedUsers) {
            blockedUser.setBlockedUserName(fullName);
            blacklistRepository.save(blockedUser);
        }

        return Map.of("message", "Синхронизация данных прошла успешно.");
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
        UUID targetUserId = userData.getId();

        Optional<BlacklistEntity> blockedUser = blacklistRepository.findByTargetUserIdAndBlockedUserId(
                targetUserId,
                blockedUserId
        );

        if (blockedUser.isEmpty()) {
            throw new NotFoundException("Пользователя с ID " + blockedUserId
                    + " нет в черном списке у пользователя с ID " + targetUserId + ".");
        }

        if (blockedUser.get().getDeletedDate() == null) {
            blockedUser.get().setDeletedDate(LocalDateTime.now());
            blacklistRepository.save(blockedUser.get());
        }
        else {
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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
        UUID targetUserId = userData.getId();

        Page<BlacklistEntity> pageBlockedUsers;
        if (paginationAndFilters.getFilters() != null) {
            Example<BlacklistEntity> example = Example.of(BlacklistEntity
                    .builder()
                    .addedDate(paginationAndFilters.getFilters().getAddedDate())
                    .blockedUserId(paginationAndFilters.getFilters().getBlockedUserId())
                    .deletedDate(paginationAndFilters.getFilters().getDeletedDate())
                    .blockedUserName(paginationAndFilters.getFilters().getBlockedUserName())
                    .targetUserId(targetUserId)
                    .build());

            pageBlockedUsers = blacklistRepository.findAll(example, pageable);
        }
        else {
            pageBlockedUsers = blacklistRepository.findAll(pageable);
        }

        List<BlacklistEntity> blockedUsers = pageBlockedUsers.getContent();
        List<BlockedUserInfoDto> blockedUserInfoDtos = new ArrayList<>();

        for (BlacklistEntity blockedUser: blockedUsers) {
            blockedUserInfoDtos.add(new BlockedUserInfoDto(blockedUser));
        }

        SearchedBlockedUsersDto searchedBlockedUsersDto = new SearchedBlockedUsersDto();
        searchedBlockedUsersDto.setBlockedUsers(blockedUserInfoDtos);
        searchedBlockedUsersDto.setFilters(paginationAndFilters.getFilters());
        searchedBlockedUsersDto.setPageInfo(paginationAndFilters.getPageInfo());

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
        UUID targetUserId = userData.getId();

        Optional<BlacklistEntity> blockedUser = blacklistRepository.findByTargetUserIdAndBlockedUserId(
                targetUserId,
                blockedUserId
        );

        return blockedUser.isPresent() && blockedUser.get().getDeletedDate() == null;
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

        return blockedUser.isPresent() && blockedUser.get().getDeletedDate() == null;
    }


}
