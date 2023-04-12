package ru.hits.messengerapi.friends.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.hits.messengerapi.common.exception.ConflictException;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.common.helpingservices.implementation.CheckPaginationInfoService;
import ru.hits.messengerapi.common.security.JwtUserData;
import ru.hits.messengerapi.friends.dto.*;
import ru.hits.messengerapi.friends.entity.FriendEntity;
import ru.hits.messengerapi.friends.repository.FriendRepository;
import ru.hits.messengerapi.friends.service.FriendServiceInterface;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FriendService implements FriendServiceInterface {

    private final FriendRepository friendRepository;
    private final CheckPaginationInfoService checkPaginationInfoService;



    @Override
    public FriendsPageListDto getFriends(PaginationDto paginationDto) {
        int pageNumber = paginationDto.getPageInfo().getPageNumber();
        int pageSize = paginationDto.getPageInfo().getPageSize();
        checkPaginationInfoService.checkPagination(pageNumber, pageSize);
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
        UUID targetUserId = userData.getId();

        List<FriendEntity> friends;
        if (paginationDto.getFullNameFilter() == null || paginationDto.getFullNameFilter().isBlank()) {
             friends = friendRepository.findAllByTargetUserIdAndDeletedDate(targetUserId, null, pageable);
        }
        else {
            String wildcardFullNameFilter = "%" + paginationDto.getFullNameFilter() + "%";
            friends = friendRepository.findAllByTargetUserIdAndFriendNameLikeAndDeletedDate(
                    targetUserId, wildcardFullNameFilter, null, pageable);
        }

        List<FriendInfoDto> friendInfoDtos = new ArrayList<>();

        for (FriendEntity friend : friends) {
            friendInfoDtos.add(new FriendInfoDto(friend));
        }

        return new FriendsPageListDto(
                friendInfoDtos,
                paginationDto.getPageInfo(),
                paginationDto.getFullNameFilter()
        );
    }

    @Override
    public FriendDto getFriend(UUID addedUserId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
        UUID targetUserId = userData.getId();

        Optional<FriendEntity> friend = friendRepository.findByTargetUserIdAndAddedUserId(
                targetUserId,
                addedUserId
        );

        if (friend.isEmpty()) {
            throw new NotFoundException("Пользователя с ID " + addedUserId
                    + " нет в списке друзей у пользователя с ID " + targetUserId + ".");
        }

        return new FriendDto(friend.get());
    }

    @Override
    public FriendDto addToFriends(AddToFriendsDto addToFriendsDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
        UUID targetUserId = userData.getId();

        if (addToFriendsDto.getAddedUserId().equals(targetUserId)) {
            throw new ConflictException("Пользователь не может добавить самого себя в друзья.");
        }

        if (friendRepository.findByTargetUserIdAndAddedUserId(
                targetUserId, addToFriendsDto.getAddedUserId()).isPresent()) {
            throw new ConflictException("Пользователь с ID " + addToFriendsDto.getAddedUserId() + " и ФИО "
                    + addToFriendsDto.getFriendName() + " уже добавлен в список друзей.");
        }

        FriendEntity friend = new FriendEntity();
        friend.setAddedDate(LocalDateTime.now());
        friend.setTargetUserId(targetUserId);
        friend.setAddedUserId(addToFriendsDto.getAddedUserId());
        friend.setFriendName(addToFriendsDto.getFriendName());

        friend = friendRepository.save(friend);

        FriendEntity mutualFriendship = new FriendEntity();
        mutualFriendship.setAddedDate(LocalDateTime.now());
        mutualFriendship.setTargetUserId(addToFriendsDto.getAddedUserId());
        mutualFriendship.setAddedUserId(targetUserId);
        mutualFriendship.setFriendName(userData.getFullName().toString());

        friendRepository.save(mutualFriendship);

        return new FriendDto(friend);
    }
}
