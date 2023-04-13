package ru.hits.messengerapi.friends.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.hits.messengerapi.common.exception.ConflictException;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.common.helpingservices.implementation.CheckPaginationInfoService;
import ru.hits.messengerapi.common.security.JwtUserData;
import ru.hits.messengerapi.common.security.props.SecurityProps;
import ru.hits.messengerapi.friends.dto.*;
import ru.hits.messengerapi.friends.dto.friends.*;
import ru.hits.messengerapi.friends.entity.FriendEntity;
import ru.hits.messengerapi.friends.repository.FriendsRepository;
import ru.hits.messengerapi.friends.service.FriendsServiceInterface;

import java.time.LocalDateTime;
import java.util.*;

import static ru.hits.messengerapi.common.security.SecurityConst.HEADER_API_KEY;

@Service
@RequiredArgsConstructor
public class FriendsService implements FriendsServiceInterface {

    private final FriendsRepository friendsRepository;
    private final CheckPaginationInfoService checkPaginationInfoService;
    private final SecurityProps securityProps;


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
             friends = friendsRepository.findAllByTargetUserIdAndDeletedDate(targetUserId, null, pageable);
        }
        else {
            String wildcardFullNameFilter = "%" + paginationDto.getFullNameFilter() + "%";
            friends = friendsRepository.findAllByTargetUserIdAndFriendNameLikeAndDeletedDate(
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

        Optional<FriendEntity> friend = friendsRepository.findByTargetUserIdAndAddedUserId(
                targetUserId,
                addedUserId
        );

        if (friend.isEmpty() || friend.get().getDeletedDate() != null) {
            throw new NotFoundException("Пользователя с ID " + addedUserId
                    + " нет в списке друзей у пользователя с ID " + targetUserId + ".");
        }

        return new FriendDto(friend.get());
    }

    @Override
    public FriendDto addToFriends(AddToFriendsDto addToFriendsDto) {
        RestTemplate restTemplate = new RestTemplate();
        String url =
                "http://localhost:8191/integration/users/check-existence";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HEADER_API_KEY, securityProps.getIntegrations().getApiKey());
        HttpEntity<AddToFriendsDto> requestEntity = new HttpEntity<>(addToFriendsDto, headers);

        ResponseEntity<String> responseEntity = restTemplate
                .exchange(url, HttpMethod.POST, requestEntity, String.class);

        if (Objects.equals(responseEntity.getBody(), "dont exist")) {
            throw new NotFoundException("Пользователя с id " + addToFriendsDto.getId()
                    + " и ФИО " + addToFriendsDto.getFullName() + " не существует.");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
        UUID targetUserId = userData.getId();

        if (addToFriendsDto.getId().equals(targetUserId)) {
            throw new ConflictException("Пользователь не может добавить самого себя в друзья.");
        }

        Optional<FriendEntity> friend = friendsRepository.findByTargetUserIdAndAddedUserId(
                targetUserId, addToFriendsDto.getId());

        if (friend.isPresent() && friend.get().getDeletedDate() == null) {
            throw new ConflictException("Пользователь с ID " + addToFriendsDto.getId() + " и ФИО "
                    + addToFriendsDto.getFullName() + " уже добавлен в список друзей.");
        }

        if (friend.isPresent()) {
            friend.get().setDeletedDate(null);
            friend.get().setAddedDate(LocalDateTime.now());
            syncFriendData(addToFriendsDto.getId());
            friendsRepository.save(friend.get());

            Optional<FriendEntity> mutualFriendship = friendsRepository.findByTargetUserIdAndAddedUserId(
                    addToFriendsDto.getId(), targetUserId);

            if (mutualFriendship.isPresent()) {
                mutualFriendship.get().setDeletedDate(null);
                mutualFriendship.get().setAddedDate(LocalDateTime.now());
                syncFriendData(targetUserId);
                friendsRepository.save(mutualFriendship.get());

                return new FriendDto(friend.get());
            }
        }

        FriendEntity newFriend = new FriendEntity();
        newFriend.setAddedDate(LocalDateTime.now());
        newFriend.setTargetUserId(targetUserId);
        newFriend.setAddedUserId(addToFriendsDto.getId());
        newFriend.setFriendName(addToFriendsDto.getFullName());

        newFriend = friendsRepository.save(newFriend);

        FriendEntity mutualFriendship = new FriendEntity();
        mutualFriendship.setAddedDate(newFriend.getAddedDate());
        mutualFriendship.setTargetUserId(addToFriendsDto.getId());
        mutualFriendship.setAddedUserId(targetUserId);
        mutualFriendship.setFriendName(userData.getFullName());

        friendsRepository.save(mutualFriendship);

        return new FriendDto(newFriend);
    }

    @Override
    public void syncFriendData(UUID id) {
        RestTemplate restTemplate = new RestTemplate();
        String url =
                "http://localhost:8191/integration/users/get-full-name";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HEADER_API_KEY, securityProps.getIntegrations().getApiKey());
        HttpEntity<UUID> requestEntity = new HttpEntity<>(id, headers);

        ResponseEntity<String> responseEntity = restTemplate
                .exchange(url, HttpMethod.POST, requestEntity, String.class);

        if (Objects.equals(responseEntity.getBody(), "dont exist")) {
            throw new NotFoundException("Пользователя с id " + id + " не существует.");
        }

        List<FriendEntity> friends = friendsRepository.findAllByAddedUserId(id);

        for (FriendEntity friend: friends) {
            friend.setFriendName(responseEntity.getBody());
            friendsRepository.save(friend);
        }
    }

    @Override
    public FriendDto deleteFriend(UUID addedUserId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
        UUID targetUserId = userData.getId();

        Optional<FriendEntity> friend = friendsRepository.findByTargetUserIdAndAddedUserId(
                targetUserId,
                addedUserId
        );

        if (friend.isEmpty()) {
            throw new NotFoundException("Пользователя с ID " + addedUserId
                    + " нет в списке друзей у пользователя с ID " + targetUserId + ".");
        }

        if (friend.get().getDeletedDate() == null) {
            Optional<FriendEntity> addedFriend = friendsRepository.findByTargetUserIdAndAddedUserId(
                    addedUserId,
                    targetUserId
            );

            if (addedFriend.isEmpty()) {
                throw new NotFoundException("Пользователя с ID " + targetUserId
                        + " нет в списке друзей у пользователя с ID " + addedUserId + ".");
            }

            friend.get().setDeletedDate(LocalDateTime.now());
            addedFriend.get().setDeletedDate(friend.get().getDeletedDate());

            friendsRepository.save(friend.get());
            friendsRepository.save(addedFriend.get());
        }
        else {
            throw new ConflictException("Пользователь с ID " + addedUserId
                    + " уже удален из списка друзей пользователя с ID " + targetUserId + ".");
        }

        return new FriendDto(friend.get());
    }

    @Override
    public SearchedFriendsDto searchFriends(PaginationWithFriendFiltersDto paginationAndFilters) {
        int pageNumber = paginationAndFilters.getPageInfo().getPageNumber();
        int pageSize = paginationAndFilters.getPageInfo().getPageSize();
        checkPaginationInfoService.checkPagination(pageNumber, pageSize);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
        UUID targetUserId = userData.getId();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        Example<FriendEntity> example = Example.of(FriendEntity
                .builder()
                .addedDate(paginationAndFilters.getFilters().getAddedDate())
                .addedUserId(paginationAndFilters.getFilters().getAddedUserId())
                .deletedDate(paginationAndFilters.getFilters().getDeletedDate())
                .friendName(paginationAndFilters.getFilters().getFriendName())
                .targetUserId(targetUserId)
                .build());

        Page<FriendEntity> friends = friendsRepository.findAll(example, pageable);
        List<FriendEntity> friendEntities = friends.getContent();
        List<FriendInfoDto> friendInfoDtos = new ArrayList<>();

        for (FriendEntity friend: friendEntities) {
            friendInfoDtos.add(new FriendInfoDto(friend));
        }

        SearchedFriendsDto searchedFriendsDto = new SearchedFriendsDto();
        searchedFriendsDto.setFriends(friendInfoDtos);
        searchedFriendsDto.setFilters(paginationAndFilters.getFilters());
        searchedFriendsDto.setPageInfo(paginationAndFilters.getPageInfo());

        return searchedFriendsDto;
    }
}
