package ru.hits.messengerapi.friends;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hits.messengerapi.friends.controller.IntegrationFriendsController;
import ru.hits.messengerapi.friends.service.IntegrationFriendsService;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IntegrationFriendsControllerTest {

    private IntegrationFriendsController integrationFriendsController;
    private IntegrationFriendsService integrationFriendsService;

    @BeforeEach
    void setUp() {
        integrationFriendsService = mock(IntegrationFriendsService.class);
        integrationFriendsController = new IntegrationFriendsController(integrationFriendsService);
    }

    @Test
    void checkExistenceInFriends_shouldReturnTrue() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        boolean existsInFriends = true;
        List<UUID> uuids = Arrays.asList(userId1, userId2);

        when(integrationFriendsService.checkExistenceInFriends(userId1, userId2)).thenReturn(existsInFriends);

        ResponseEntity<Boolean> response = integrationFriendsController.checkExistenceInFriends(uuids);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(existsInFriends, response.getBody());
    }

    @Test
    void checkMultiExistenceInFriends_shouldReturnTrue() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        boolean existsInFriends = true;
        List<UUID> uuids = Arrays.asList(userId1, userId2);

        when(integrationFriendsService.checkMultiExistenceInFriends(uuids)).thenReturn(existsInFriends);

        ResponseEntity<Boolean> response = integrationFriendsController.checkMultiExistenceInFriends(uuids);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(existsInFriends, response.getBody());
    }
}
