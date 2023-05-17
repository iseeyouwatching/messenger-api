package ru.hits.messengerapi.friends;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.hits.messengerapi.common.dto.UserIdAndFullNameDto;
import ru.hits.messengerapi.common.security.props.SecurityProps;
import ru.hits.messengerapi.friends.entity.BlacklistEntity;
import ru.hits.messengerapi.friends.entity.FriendEntity;
import ru.hits.messengerapi.friends.repository.BlacklistRepository;
import ru.hits.messengerapi.friends.repository.FriendsRepository;
import ru.hits.messengerapi.friends.service.IntegrationRequestsService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

class IntegrationRequestsServiceTest {

    private IntegrationRequestsService integrationRequestsService;

    @Mock
    private BlacklistRepository blacklistRepository;

    @Mock
    private FriendsRepository friendsRepository;

    @Mock
    private SecurityProps securityProps;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        integrationRequestsService = new IntegrationRequestsService(
                blacklistRepository,
                friendsRepository,
                securityProps
        );
    }

    @Test
    void syncBlockedUserData_shouldSyncBlockedUserDataAndLogInfo() {
        UserIdAndFullNameDto userIdAndFullNameDto = new UserIdAndFullNameDto();
        UUID userId = UUID.randomUUID();
        userIdAndFullNameDto.setId(userId);
        userIdAndFullNameDto.setFullName("John Doe");

        List<BlacklistEntity> blockedUsers = new ArrayList<>();
        BlacklistEntity blockedUser1 = new BlacklistEntity();
        blockedUser1.setBlockedUserId(userId);
        blockedUsers.add(blockedUser1);
        BlacklistEntity blockedUser2 = new BlacklistEntity();
        blockedUser2.setBlockedUserId(userId);
        blockedUsers.add(blockedUser2);

        when(blacklistRepository.findAllByBlockedUserId(userId)).thenReturn(blockedUsers);

        integrationRequestsService.syncBlockedUserData(userIdAndFullNameDto);

        verify(blacklistRepository, times(1)).findAllByBlockedUserId(userId);
        verify(blacklistRepository, times(1)).saveAll(blockedUsers);
    }

    @Test
    void syncFriendData_shouldSyncFriendDataAndLogInfo() {
        UserIdAndFullNameDto userIdAndFullNameDto = new UserIdAndFullNameDto();
        UUID userId = UUID.randomUUID();
        userIdAndFullNameDto.setId(userId);
        userIdAndFullNameDto.setFullName("John Doe");

        List<FriendEntity> friends = new ArrayList<>();
        FriendEntity friend1 = new FriendEntity();
        friend1.setAddedUserId(userId);
        friends.add(friend1);
        FriendEntity friend2 = new FriendEntity();
        friend2.setAddedUserId(userId);
        friends.add(friend2);

        when(friendsRepository.findAllByAddedUserId(userId)).thenReturn(friends);

        integrationRequestsService.syncFriendData(userIdAndFullNameDto);

        verify(friendsRepository, times(1)).findAllByAddedUserId(userId);
        verify(friendsRepository, times(1)).saveAll(friends);
    }
}
