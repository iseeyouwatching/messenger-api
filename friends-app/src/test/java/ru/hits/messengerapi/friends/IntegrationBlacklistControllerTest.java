package ru.hits.messengerapi.friends;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hits.messengerapi.friends.controller.IntegrationBlacklistController;
import ru.hits.messengerapi.friends.service.BlacklistService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IntegrationBlacklistControllerTest {

    private IntegrationBlacklistController integrationBlacklistController;
    private BlacklistService blacklistService;

    @BeforeEach
    void setUp() {
        blacklistService = mock(BlacklistService.class);
        integrationBlacklistController = new IntegrationBlacklistController(blacklistService);
    }

    @Test
    void checkIfTheUserBlacklisted_shouldReturnTrue() {
        UUID targetUserId = UUID.randomUUID();
        UUID blockedUserId = UUID.randomUUID();
        boolean isBlacklisted = true;
        Map<String, UUID> requestMap = new HashMap<>();
        requestMap.put("targetUserId", targetUserId);
        requestMap.put("blockedUserId", blockedUserId);

        when(blacklistService.checkIfTheTargetUserBlacklisted(targetUserId, blockedUserId)).thenReturn(isBlacklisted);

        ResponseEntity<Boolean> response = integrationBlacklistController.checkIfTheUserBlacklisted(requestMap);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(isBlacklisted, response.getBody());
    }
}
