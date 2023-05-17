package ru.hits.messengerapi.friends;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hits.messengerapi.friends.controller.BlacklistController;
import ru.hits.messengerapi.friends.dto.blacklist.BlockedUserDto;
import ru.hits.messengerapi.friends.dto.blacklist.BlockedUsersPageListDto;
import ru.hits.messengerapi.friends.dto.blacklist.PaginationWithBlockedUserFiltersDto;
import ru.hits.messengerapi.friends.dto.blacklist.SearchedBlockedUsersDto;
import ru.hits.messengerapi.friends.dto.common.AddPersonDto;
import ru.hits.messengerapi.friends.dto.common.PaginationWithFullNameFilterDto;
import ru.hits.messengerapi.friends.service.BlacklistService;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BlacklistControllerTest {

    private BlacklistController blacklistController;
    private BlacklistService blacklistService;

    @BeforeEach
    void setUp() {
        blacklistService = mock(BlacklistService.class);
        blacklistController = new BlacklistController(blacklistService);
    }

    @Test
    void getBlockedUsers_shouldReturnBlockedUsersPageListDto() {
        PaginationWithFullNameFilterDto filterDto = new PaginationWithFullNameFilterDto();
        BlockedUsersPageListDto expectedResponse = new BlockedUsersPageListDto();

        when(blacklistService.getBlockedUsers(filterDto)).thenReturn(expectedResponse);

        ResponseEntity<BlockedUsersPageListDto> response = blacklistController.getBlockedUsers(filterDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void getBlockedUser_shouldReturnBlockedUserDto() {
        UUID userId = UUID.randomUUID();
        BlockedUserDto expectedResponse = new BlockedUserDto();

        when(blacklistService.getBlockedUser(userId)).thenReturn(expectedResponse);

        ResponseEntity<BlockedUserDto> response = blacklistController.getBlockedUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void addToBlacklist_shouldReturnBlockedUserDto() {
        AddPersonDto addPersonDto = new AddPersonDto();
        BlockedUserDto expectedResponse = new BlockedUserDto();

        when(blacklistService.addToBlacklist(addPersonDto)).thenReturn(expectedResponse);

        ResponseEntity<BlockedUserDto> response = blacklistController.addToBlacklist(addPersonDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void deleteFromBlacklist_shouldReturnBlockedUserDto() {
        UUID userId = UUID.randomUUID();
        BlockedUserDto expectedResponse = new BlockedUserDto();

        when(blacklistService.deleteFromBlacklist(userId)).thenReturn(expectedResponse);

        ResponseEntity<BlockedUserDto> response = blacklistController.deleteFromBlacklist(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void searchBlockedUsers_shouldReturnSearchedBlockedUsersDto() {
        PaginationWithBlockedUserFiltersDto filtersDto = new PaginationWithBlockedUserFiltersDto();
        SearchedBlockedUsersDto expectedResponse = new SearchedBlockedUsersDto(new ArrayList<>(), null, null);

        when(blacklistService.searchBlockedUsers(filtersDto)).thenReturn(expectedResponse);

        ResponseEntity<SearchedBlockedUsersDto> response = blacklistController.searchBlockedUsers(filtersDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void checkIfTheUserBlacklisted_shouldReturnTrue() {
        UUID userId = UUID.randomUUID();
        boolean expectedResponse = true;

        when(blacklistService.checkIfTheUserBlacklisted(userId)).thenReturn(expectedResponse);

        ResponseEntity<Boolean> response = blacklistController.checkIfTheUserBlacklisted(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

}
