package ru.hits.messengerapi.friends;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.hits.messengerapi.common.helpingservices.CheckPaginationInfoService;
import ru.hits.messengerapi.common.security.JwtUserData;
import ru.hits.messengerapi.friends.dto.common.PageInfoDto;
import ru.hits.messengerapi.friends.dto.common.PaginationWithFullNameFilterDto;
import ru.hits.messengerapi.friends.dto.friends.FriendDto;
import ru.hits.messengerapi.friends.dto.friends.FriendsPageListDto;
import ru.hits.messengerapi.friends.dto.friends.PaginationWithFriendFiltersDto;
import ru.hits.messengerapi.friends.dto.friends.SearchedFriendsDto;
import ru.hits.messengerapi.friends.entity.FriendEntity;
import ru.hits.messengerapi.friends.repository.FriendsRepository;
import ru.hits.messengerapi.friends.service.BlacklistService;
import ru.hits.messengerapi.friends.service.FriendsService;
import ru.hits.messengerapi.friends.service.IntegrationRequestsService;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FriendsServiceTest {

    @InjectMocks
    private FriendsService friendsService;

    @Mock
    private FriendsRepository friendsRepository;

    @Mock
    private CheckPaginationInfoService checkPaginationInfoService;

    @Mock
    private IntegrationRequestsService integrationRequestsService;

    @Mock
    private BlacklistService blacklistService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getFriends_shouldReturnFriendsPageListDto() {
        PaginationWithFullNameFilterDto paginationDto = new PaginationWithFullNameFilterDto();
        paginationDto.setPageInfo(new PageInfoDto(1, 10));
        paginationDto.setFullNameFilter("John Doe");

        UUID targetUserId = UUID.randomUUID();
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = mock(Authentication.class);

        JwtUserData jwtUserData = new JwtUserData("gsagvxcvz", targetUserId, "test");
        when(authentication.getPrincipal()).thenReturn(jwtUserData);

        securityContext.setAuthentication(authentication);

        Pageable pageable = PageRequest.of(0, 10);
        List<FriendEntity> friendsList = Collections.singletonList(new FriendEntity());
        when(friendsRepository.findAllByTargetUserIdAndFriendNameLikeIgnoreCaseAndDeletedDate(
                eq(targetUserId), anyString(), eq(null), eq(pageable)))
                .thenReturn(friendsList);

        FriendsPageListDto result = friendsService.getFriends(paginationDto);

        assertNotNull(result);
        assertEquals(friendsList.size(), result.getFriends().size());
    }

    @Test
    void getFriend_shouldReturnFriendDto() {
        UUID targetUserId = UUID.randomUUID();
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = mock(Authentication.class);

        JwtUserData jwtUserData = new JwtUserData("gsagvxcvz", targetUserId, "test");
        when(authentication.getPrincipal()).thenReturn(jwtUserData);

        securityContext.setAuthentication(authentication);

        UUID addedUserId = UUID.randomUUID();
        FriendEntity friendEntity = new FriendEntity();
        when(friendsRepository.findByTargetUserIdAndAddedUserId(eq(targetUserId), eq(addedUserId)))
                .thenReturn(Optional.of(friendEntity));

        FriendDto result = friendsService.getFriend(addedUserId);

        assertNotNull(result);
    }

    @Test
    void searchFriends_shouldReturnSearchedFriendsDto() {
        PaginationWithFriendFiltersDto paginationAndFilters = new PaginationWithFriendFiltersDto();
        paginationAndFilters.setPageInfo(new PageInfoDto(1, 10));

        UUID targetUserId = UUID.randomUUID();
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = mock(Authentication.class);

        JwtUserData jwtUserData = new JwtUserData("gsagvxcvz", targetUserId, "test");
        when(authentication.getPrincipal()).thenReturn(jwtUserData);

        securityContext.setAuthentication(authentication);

        Pageable pageable = PageRequest.of(0, 10);
        Page<FriendEntity> pageFriends = mock(Page.class);
        when(friendsRepository.findAll(any(Example.class), eq(pageable))).thenReturn(pageFriends);

        List<FriendEntity> friends = new ArrayList<>();
        FriendEntity friend1 = new FriendEntity();
        friend1.setDeletedDate(LocalDate.now());
        friends.add(friend1);
        FriendEntity friend2 = new FriendEntity();
        friend2.setDeletedDate(friend1.getDeletedDate());
        friends.add(friend2);
        when(pageFriends.getContent()).thenReturn(friends);

        SearchedFriendsDto result = friendsService.searchFriends(paginationAndFilters);

        assertNotNull(result);
        assertEquals(0, result.getFriends().size());
        assertEquals(paginationAndFilters.getFilters(), result.getFilters());
        assertEquals(paginationAndFilters.getPageInfo(), result.getPageInfo());
    }

}
