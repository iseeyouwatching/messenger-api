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
import ru.hits.messengerapi.friends.dto.common.PaginationDto;
import ru.hits.messengerapi.friends.entity.BlacklistEntity;
import ru.hits.messengerapi.friends.entity.FriendEntity;
import ru.hits.messengerapi.friends.repository.BlacklistRepository;
import ru.hits.messengerapi.friends.repository.FriendsRepository;
import ru.hits.messengerapi.friends.service.BlacklistServiceInterface;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BlacklistService implements BlacklistServiceInterface {

    private final BlacklistRepository blacklistRepository;
    private final CheckPaginationInfoService checkPaginationInfoService;
    private final IntegrationRequestsService integrationRequestsService;
    private final FriendsService friendsService;
    private final FriendsRepository friendsRepository;

    @Override
    public BlockedUsersPageListDto getBlockedUsers(PaginationDto paginationDto) {
        int pageNumber = paginationDto.getPageInfo().getPageNumber();
        int pageSize = paginationDto.getPageInfo().getPageSize();
        checkPaginationInfoService.checkPagination(pageNumber, pageSize);
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
        UUID targetUserId = userData.getId();

        List<BlacklistEntity> blockedUsers;
        if (paginationDto.getFullNameFilter() == null || paginationDto.getFullNameFilter().isBlank()) {
            blockedUsers = blacklistRepository
                    .findAllByTargetUserIdAndDeletedDate(targetUserId, null, pageable);
        }
        else {
            String wildcardFullNameFilter = "%" + paginationDto.getFullNameFilter() + "%";
            blockedUsers = blacklistRepository.findAllByTargetUserIdAndBlockedUserNameLikeAndDeletedDate(
                    targetUserId, wildcardFullNameFilter, null, pageable);
        }

        List<BlockedUserInfoDto> blockedUserInfoDtos = new ArrayList<>();
        for (BlacklistEntity blockedUser : blockedUsers) {
            blockedUserInfoDtos.add(new BlockedUserInfoDto(blockedUser));
        }

        return new BlockedUsersPageListDto(
                blockedUserInfoDtos,
                paginationDto.getPageInfo(),
                paginationDto.getFullNameFilter()
        );
    }

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

    @Override
    public boolean checkIfTheTargetUserBlacklisted(UUID targetUserId, UUID blockedUserId) {
        Optional<BlacklistEntity> blockedUser = blacklistRepository.findByTargetUserIdAndBlockedUserId(
                targetUserId,
                blockedUserId
        );

        return blockedUser.isPresent() && blockedUser.get().getDeletedDate() == null;
    }


}
