package ru.hits.messengerapi.notifications;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import ru.hits.messengerapi.common.dto.NewNotificationDto;
import ru.hits.messengerapi.common.enumeration.NotificationType;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.common.helpingservices.CheckPaginationInfoService;
import ru.hits.messengerapi.common.security.JwtUserData;
import ru.hits.messengerapi.notifications.dto.NotificationsStatusUpdateDTO;
import ru.hits.messengerapi.notifications.entity.NotificationEntity;
import ru.hits.messengerapi.notifications.enumeration.NotificationStatus;
import ru.hits.messengerapi.notifications.repository.NotificationRepository;
import ru.hits.messengerapi.notifications.service.NotificationService;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private CheckPaginationInfoService checkPaginationInfoService;

    @InjectMocks
    private NotificationService notificationService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetNotification() {
        NewNotificationDto newNotificationDto = new NewNotificationDto();
        newNotificationDto.setType(NotificationType.LOGIN);
        newNotificationDto.setText("text");
        newNotificationDto.setUserId(UUID.randomUUID());

        NotificationRepository notificationRepository = mock(NotificationRepository.class);

        NotificationService notificationService = new NotificationService(notificationRepository,
                checkPaginationInfoService);

        NotificationEntity savedNotification = new NotificationEntity();
        when(notificationRepository.save(any(NotificationEntity.class))).thenReturn(savedNotification);

        notificationService.getNotification(newNotificationDto);

        verify(notificationRepository, times(1)).save(any(NotificationEntity.class));
    }

    @Test
    @WithMockUser
    public void testGetUnreadCount() {
        UUID userId = UUID.randomUUID();
        long count = 0;

        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = mock(Authentication.class);

        JwtUserData jwtUserData = new JwtUserData("gsagvxcvz", userId, "test");
        when(authentication.getPrincipal()).thenReturn(jwtUserData);

        securityContext.setAuthentication(authentication);

        when(notificationRepository.countByUserIdAndStatus(userId, NotificationStatus.UNREAD)).thenReturn(count);

        Long result = notificationService.getUnreadCount();

        assertEquals(count, result.longValue());
    }


    @Test(expected = NotFoundException.class)
    public void testMarkNotificationsAsReadOrUnread_NotFoundException() {
        NotificationsStatusUpdateDTO notificationsStatusUpdateDTO = new NotificationsStatusUpdateDTO();
        UUID notificationId = UUID.randomUUID();
        notificationsStatusUpdateDTO.setNotificationsIDs(Arrays.asList(notificationId));
        notificationsStatusUpdateDTO.setStatus(NotificationStatus.READ);
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

        notificationService.markNotificationsAsReadOrUnread(notificationsStatusUpdateDTO);
    }

}
