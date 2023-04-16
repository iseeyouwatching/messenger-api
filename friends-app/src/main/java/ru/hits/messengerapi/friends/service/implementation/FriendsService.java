package ru.hits.messengerapi.friends.service.implementation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
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
import ru.hits.messengerapi.friends.dto.common.AddPersonDto;
import ru.hits.messengerapi.friends.dto.common.PaginationWithFullNameFilterDto;
import ru.hits.messengerapi.friends.dto.friends.*;
import ru.hits.messengerapi.friends.entity.FriendEntity;
import ru.hits.messengerapi.friends.repository.FriendsRepository;
import ru.hits.messengerapi.friends.service.FriendsServiceInterface;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 *  Сервис друзей.
 */
@Service
@Slf4j
public class FriendsService implements FriendsServiceInterface {

    /**
     * Репозиторий для работы с сущностью {@link FriendEntity}.
     */
    private final FriendsRepository friendsRepository;

    /**
     * Вспомогательный сервис для проверки данных для пагинации.
     */
    private final CheckPaginationInfoService checkPaginationInfoService;

    /**
     * Сервис, в котором хранится логика отправки интеграционных запросов.
     */
    private final IntegrationRequestsService integrationRequestsService;

    /**
     *  Сервис черного списка.
     */
    private final BlacklistService blacklistService;


    /**
     * Конструктор класса {@link FriendsService}.
     *
     * @param friendsRepository репозиторий друзей.
     * @param checkPaginationInfoService вспомогательный сервис для проверки данных для пагинации.
     * @param integrationRequestsService сервис, в котором хранится логика отправки интеграционных запросов.
     * @param blacklistService сервис черного списка.
     */
    public FriendsService(FriendsRepository friendsRepository,
                          CheckPaginationInfoService checkPaginationInfoService,
                          IntegrationRequestsService integrationRequestsService,
                          @Lazy BlacklistService blacklistService) {
        this.friendsRepository = friendsRepository;
        this.checkPaginationInfoService = checkPaginationInfoService;
        this.integrationRequestsService = integrationRequestsService;
        this.blacklistService = blacklistService;
    }

    /**
     * Получить друзей целевого пользователя.
     *
     * @param paginationWithFullNameFilterDto информация о пагинации и фильтре по ФИО.
     * @return список друзей, информация о странице и фильтре по ФИО.
     */
    @Override
    public FriendsPageListDto getFriends(PaginationWithFullNameFilterDto paginationWithFullNameFilterDto) {
        int pageNumber = paginationWithFullNameFilterDto.getPageInfo().getPageNumber();
        int pageSize = paginationWithFullNameFilterDto.getPageInfo().getPageSize();
        checkPaginationInfoService.checkPagination(pageNumber, pageSize);
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        UUID targetUserId = getAuthenticatedUserId();

        List<FriendEntity> friends;
        if (paginationWithFullNameFilterDto.getFullNameFilter() == null ||
                paginationWithFullNameFilterDto.getFullNameFilter().isBlank()) {
             friends = friendsRepository.findAllByTargetUserIdAndDeletedDate(targetUserId, null,
                     pageable);
        }
        else {
            String wildcardFullNameFilter = "%" + paginationWithFullNameFilterDto.getFullNameFilter() + "%";
            friends = friendsRepository.findAllByTargetUserIdAndFriendNameLikeAndDeletedDate(
                    targetUserId, wildcardFullNameFilter, null, pageable);
        }

        List<FriendInfoDto> friendInfoDtos = friends.stream()
                .map(FriendInfoDto::new)
                .collect(Collectors.toList());

        log.info("Получение списка друзей для пользователя с ID {}.", targetUserId);
        return new FriendsPageListDto(
                friendInfoDtos,
                paginationWithFullNameFilterDto.getPageInfo(),
                paginationWithFullNameFilterDto.getFullNameFilter()
        );
    }

    /**
     * Получить информацию о друге.
     *
     * @param addedUserId id друга.
     * @return полная информация о друге.
     * @throws NotFoundException если пользователя нет в списке друзей.
     */
    @Override
    public FriendDto getFriend(UUID addedUserId) {
        UUID targetUserId = getAuthenticatedUserId();

        Optional<FriendEntity> friend = friendsRepository.findByTargetUserIdAndAddedUserId(
                targetUserId,
                addedUserId
        );

        if (friend.isEmpty() || friend.get().getDeletedDate() != null) {
            log.error("Пользователя с ID {} нет в списке друзей у пользователя с ID {}.",
                    addedUserId, targetUserId);
            throw new NotFoundException("Пользователя с ID " + addedUserId
                    + " нет в списке друзей у пользователя с ID " + targetUserId + ".");
        }

        return new FriendDto(friend.get());
    }

    /**
     * Добавить пользователя в друзья.
     *
     * @param addPersonDto информация необходимая для добавления в друзья.
     * @return полная информация о друге.
     * @throws ConflictException если 1) пользователь хочет добавить самого себя в друзья;
     *                                2) пользователь хочет добавить в друзья пользователя,
     *                                который уже добавлен к нему в друзья;
     *                                3) пользователь хочет добавить в друзья пользователя,
     *                                который находится у него в ЧС;
     *                                4) пользователь хочет добавить в друзья пользователя,
     *                                у которого находится в ЧС
     */
    @Override
    public FriendDto addToFriends(AddPersonDto addPersonDto) {
        integrationRequestsService.checkUserExistence(addPersonDto);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
        UUID targetUserId = userData.getId();
        String targetUserFullName = userData.getFullName();
        log.info("Пользователь {} ({}) пытается добавить пользователя {} ({}) в список друзей.",
                targetUserId, targetUserFullName, addPersonDto.getId(), addPersonDto.getFullName());

        if (addPersonDto.getId().equals(targetUserId)) {
            throw new ConflictException("Пользователь не может добавить самого себя в друзья.");
        }

        Optional<FriendEntity> friend = friendsRepository.findByTargetUserIdAndAddedUserId(
                targetUserId, addPersonDto.getId());

        if (friend.isPresent() && friend.get().getDeletedDate() == null) {
            String message = "Пользователь с ID " + addPersonDto.getId() + " и ФИО " +
                    addPersonDto.getFullName() + " уже добавлен в список друзей.";
            log.error(message);
            throw new ConflictException(message);
        }

        if (blacklistService.checkIfTheUserBlacklisted(addPersonDto.getId())) {
            String message = "Пользователь с ID " + addPersonDto.getId() + " и ФИО " + addPersonDto.getFullName() +
                    " находится у пользователя с ID " + targetUserId + " в черном списке.";
            log.error(message);
            throw new ConflictException(message);
        }

        if (blacklistService.checkIfTheTargetUserBlacklisted(addPersonDto.getId(), targetUserId)) {
            String message = "Пользователь с ID " + targetUserId + " и ФИО " + targetUserFullName +
                    " не может добавить пользователя с ID " + addPersonDto.getId() +
                    " в друзья, так как находится у него в черном списке.";
            log.error(message);
            throw new ConflictException(message);
        }

        if (friend.isPresent()) {
            friend.get().setDeletedDate(null);
            friend.get().setAddedDate(LocalDateTime.now());
            integrationRequestsService.syncFriendData(addPersonDto.getId());
            friendsRepository.save(friend.get());

            Optional<FriendEntity> mutualFriendship = friendsRepository.findByTargetUserIdAndAddedUserId(
                    addPersonDto.getId(), targetUserId);

            if (mutualFriendship.isPresent()) {
                mutualFriendship.get().setDeletedDate(null);
                mutualFriendship.get().setAddedDate(friend.get().getAddedDate());
                integrationRequestsService.syncFriendData(targetUserId);
                friendsRepository.save(mutualFriendship.get());

                log.info("Пользователи {} и {} стали друзьями.", targetUserId, addPersonDto.getId());
                return new FriendDto(friend.get());
            }
        }

        FriendEntity newFriend = new FriendEntity();
        newFriend.setAddedDate(LocalDateTime.now());
        newFriend.setTargetUserId(targetUserId);
        newFriend.setAddedUserId(addPersonDto.getId());
        newFriend.setFriendName(addPersonDto.getFullName());
        newFriend = friendsRepository.save(newFriend);

        FriendEntity mutualFriendship = new FriendEntity();
        mutualFriendship.setAddedDate(newFriend.getAddedDate());
        mutualFriendship.setTargetUserId(addPersonDto.getId());
        mutualFriendship.setAddedUserId(targetUserId);
        mutualFriendship.setFriendName(userData.getFullName());
        friendsRepository.save(mutualFriendship);

        log.info("Пользователи {} и {} стали друзьями.", targetUserId, addPersonDto.getId());

        return new FriendDto(newFriend);
    }

    /**
     * Удалить пользователя из друзей.
     *
     * @param addedUserId id друга.
     * @return полная информация об удаленном друге.
     * @throws NotFoundException если пользователя нет в списке друзей.
     * @throws ConflictException если пользователь, которого хочет добавить в друзья целевой пользователь,
     *                           уже удален из друзей
     */
    @Override
    public FriendDto deleteFriend(UUID addedUserId) {
        UUID targetUserId = getAuthenticatedUserId();

        Optional<FriendEntity> friend = friendsRepository.findByTargetUserIdAndAddedUserId(
                targetUserId,
                addedUserId
        );

        if (friend.isEmpty()) {
            log.error("Пользователя с ID {} нет в списке друзей у пользователя с ID {}.",
                    addedUserId, targetUserId);
            throw new NotFoundException("Пользователя с ID " + addedUserId
                    + " нет в списке друзей у пользователя с ID " + targetUserId + ".");
        }

        if (friend.get().getDeletedDate() == null) {
            Optional<FriendEntity> addedFriend = friendsRepository.findByTargetUserIdAndAddedUserId(
                    addedUserId,
                    targetUserId
            );

            if (addedFriend.isEmpty()) {
                log.error("Пользователя с ID {} нет в списке друзей у пользователя с ID {}.",
                        targetUserId, addedUserId);
                throw new NotFoundException("Пользователя с ID " + targetUserId
                        + " нет в списке друзей у пользователя с ID " + addedUserId + ".");
            }

            friend.get().setDeletedDate(LocalDateTime.now());
            addedFriend.get().setDeletedDate(friend.get().getDeletedDate());

            friendsRepository.save(friend.get());
            friendsRepository.save(addedFriend.get());

            log.info("Пользователь с ID {} удален из списка друзей пользователя с ID {}.",
                    addedUserId, targetUserId);
        }
        else {
            log.error("Пользователь с ID {} уже удален из списка друзей пользователя с ID {}.",
                    addedUserId, targetUserId);
            throw new ConflictException("Пользователь с ID " + addedUserId
                    + " уже удален из списка друзей пользователя с ID " + targetUserId + ".");
        }

        return new FriendDto(friend.get());
    }

    /**
     * Поиск среди друзей.
     *
     * @param paginationAndFilters информация о пагинации и фильтрах.
     * @return найденные друзья с информацией о странице и фильтрах.
     */
    @Override
    public SearchedFriendsDto searchFriends(PaginationWithFriendFiltersDto paginationAndFilters) {
        int pageNumber = paginationAndFilters.getPageInfo().getPageNumber();
        int pageSize = paginationAndFilters.getPageInfo().getPageSize();
        checkPaginationInfoService.checkPagination(pageNumber, pageSize);
        log.info("Начинается поиск друзей. Страница {} размер {}", pageNumber, pageSize);
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        UUID targetUserId = getAuthenticatedUserId();
        log.debug("Идентификатор текущего пользователя: {}", targetUserId);

        Page<FriendEntity> pageFriends;
        Example<FriendEntity> example;
        if (paginationAndFilters.getFilters() != null) {
            example = Example.of(FriendEntity
                    .builder()
                    .addedDate(paginationAndFilters.getFilters().getAddedDate())
                    .addedUserId(paginationAndFilters.getFilters().getAddedUserId())
                    .friendName(paginationAndFilters.getFilters().getFriendName())
                    .targetUserId(targetUserId)
                    .build()
            );
        }
        else {
            example = Example.of(FriendEntity
                    .builder()
                    .targetUserId(targetUserId)
                    .build()
            );
        }
        pageFriends = friendsRepository.findAll(example, pageable);

        List<FriendEntity> friends = pageFriends.getContent();
        List<FriendInfoDto> friendInfoDtos = new ArrayList<>();

        for (FriendEntity friend: friends) {
            if (friend.getDeletedDate() == null) {
                friendInfoDtos.add(new FriendInfoDto(friend));
            }
        }

        SearchedFriendsDto searchedFriendsDto = new SearchedFriendsDto();
        searchedFriendsDto.setFriends(friendInfoDtos);
        searchedFriendsDto.setFilters(paginationAndFilters.getFilters());
        searchedFriendsDto.setPageInfo(paginationAndFilters.getPageInfo());
        log.info("Поиск друзей завершен. Найдено {} друзей", friendInfoDtos.size());

        return searchedFriendsDto;
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
