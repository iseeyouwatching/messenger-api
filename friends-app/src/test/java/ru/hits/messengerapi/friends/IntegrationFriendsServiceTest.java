package ru.hits.messengerapi.friends;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.hits.messengerapi.common.exception.ForbiddenException;
import ru.hits.messengerapi.friends.entity.FriendEntity;
import ru.hits.messengerapi.friends.repository.FriendsRepository;
import ru.hits.messengerapi.friends.service.IntegrationFriendsService;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IntegrationFriendsServiceTest {

    @Mock
    private FriendsRepository friendsRepository;

    private IntegrationFriendsService integrationFriendsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        integrationFriendsService = new IntegrationFriendsService(friendsRepository);
    }

    @Test
    void checkExistenceInFriends_shouldReturnTrue_whenFriendExists() {
        UUID targetUserId = UUID.randomUUID();
        UUID addedUserId = UUID.randomUUID();

        FriendEntity friend = new FriendEntity();
        friend.setDeletedDate(null);
        when(friendsRepository.findByTargetUserIdAndAddedUserId(targetUserId, addedUserId))
                .thenReturn(Optional.of(friend));

        Boolean result = integrationFriendsService.checkExistenceInFriends(targetUserId, addedUserId);

        assertTrue(result);
        verify(friendsRepository, times(1))
                .findByTargetUserIdAndAddedUserId(targetUserId, addedUserId);
    }

    @Test
    void checkExistenceInFriends_shouldThrowForbiddenException_whenFriendDoesNotExist() {
        UUID targetUserId = UUID.randomUUID();
        UUID addedUserId = UUID.randomUUID();

        when(friendsRepository.findByTargetUserIdAndAddedUserId(targetUserId, addedUserId))
                .thenReturn(Optional.empty());

        assertThrows(ForbiddenException.class,
                () -> integrationFriendsService.checkExistenceInFriends(targetUserId, addedUserId));
        verify(friendsRepository, times(1))
                .findByTargetUserIdAndAddedUserId(targetUserId, addedUserId);
    }

    @Test
    void checkExistenceInFriends_shouldThrowForbiddenException_whenFriendIsDeleted() {
        UUID targetUserId = UUID.randomUUID();
        UUID addedUserId = UUID.randomUUID();

        FriendEntity friend = new FriendEntity();
        friend.setDeletedDate(LocalDate.now());
        when(friendsRepository.findByTargetUserIdAndAddedUserId(targetUserId, addedUserId))
                .thenReturn(Optional.of(friend));

        assertThrows(ForbiddenException.class,
                () -> integrationFriendsService.checkExistenceInFriends(targetUserId, addedUserId));
        verify(friendsRepository, times(1))
                .findByTargetUserIdAndAddedUserId(targetUserId, addedUserId);
    }

}
