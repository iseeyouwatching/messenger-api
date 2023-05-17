package ru.hits.messengerapi.friends;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hits.messengerapi.friends.controller.FriendsController;
import ru.hits.messengerapi.friends.dto.common.AddPersonDto;
import ru.hits.messengerapi.friends.dto.common.PaginationWithFullNameFilterDto;
import ru.hits.messengerapi.friends.dto.friends.*;
import ru.hits.messengerapi.friends.service.FriendsService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FriendsControllerTest {

    private FriendsController friendsController;
    private FriendsService friendsService;

    @BeforeEach
    void setUp() {
        friendsService = mock(FriendsService.class);
        friendsController = new FriendsController(friendsService);
    }

    @Test
    void getFriends_shouldReturnFriendsPageListDto() {
        PaginationWithFullNameFilterDto filterDto = new PaginationWithFullNameFilterDto();
        FriendsPageListDto expectedResponse = new FriendsPageListDto();

        when(friendsService.getFriends(filterDto)).thenReturn(expectedResponse);

        ResponseEntity<FriendsPageListDto> response = friendsController.getFriends(filterDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void getFriend_shouldReturnFriendDto() {
        UUID friendId = UUID.randomUUID();
        FriendDto expectedResponse = new FriendDto();

        when(friendsService.getFriend(friendId)).thenReturn(expectedResponse);

        ResponseEntity<FriendDto> response = friendsController.getFriend(friendId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void addToFriends_shouldReturnFriendDto() {
        AddPersonDto addPersonDto = new AddPersonDto();
        FriendDto expectedResponse = new FriendDto();

        when(friendsService.addToFriends(addPersonDto)).thenReturn(expectedResponse);

        ResponseEntity<FriendDto> response = friendsController.addToFriends(addPersonDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void deleteFriend_shouldReturnFriendDto() {
        UUID friendId = UUID.randomUUID();
        FriendDto expectedResponse = new FriendDto();

        when(friendsService.deleteFriend(friendId)).thenReturn(expectedResponse);

        ResponseEntity<FriendDto> response = friendsController.deleteFriend(friendId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void searchFriends_shouldReturnSearchedFriendsDto() {
        PaginationWithFriendFiltersDto filtersDto = new PaginationWithFriendFiltersDto();
        SearchedFriendsDto expectedResponse = new SearchedFriendsDto();

        when(friendsService.searchFriends(filtersDto)).thenReturn(expectedResponse);

        ResponseEntity<SearchedFriendsDto> response = friendsController.searchFriends(filtersDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }
}