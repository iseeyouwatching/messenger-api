package ru.hits.messengerapi.notifications;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hits.messengerapi.notifications.controller.NotificationController;
import ru.hits.messengerapi.notifications.dto.NotificationsPageListDto;
import ru.hits.messengerapi.notifications.dto.NotificationsStatusUpdateDTO;
import ru.hits.messengerapi.notifications.dto.PaginationAndFiltersDto;
import ru.hits.messengerapi.notifications.service.NotificationService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    @Test
    public void testGetNotifications() {
        PaginationAndFiltersDto paginationAndFiltersDto = new PaginationAndFiltersDto();
        NotificationsPageListDto notificationsPageListDto = new NotificationsPageListDto();

        when(notificationService.getNotifications(paginationAndFiltersDto)).thenReturn(notificationsPageListDto);

        ResponseEntity<NotificationsPageListDto> response = notificationController.getNotifications(paginationAndFiltersDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(notificationsPageListDto, response.getBody());
    }

    @Test
    public void testGetUnreadCount() {
        Long unreadCount = 5L;

        when(notificationService.getUnreadCount()).thenReturn(unreadCount);

        ResponseEntity<Long> response = notificationController.getUnreadCount();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(unreadCount, response.getBody());
    }

    @Test
    public void testMarkAsReadOrUnread() {
        NotificationsStatusUpdateDTO notificationsStatusUpdateDTO = new NotificationsStatusUpdateDTO();
        Long updatedNotificationsCount = 3L;

        when(notificationService.markNotificationsAsReadOrUnread(notificationsStatusUpdateDTO)).thenReturn(updatedNotificationsCount);

        ResponseEntity<Long> response = notificationController.markAsReadOrUnread(notificationsStatusUpdateDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedNotificationsCount, response.getBody());
    }

}
