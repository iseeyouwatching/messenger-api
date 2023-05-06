package ru.hits.messengerapi.notifications.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hits.messengerapi.notifications.dto.NotificationsStatusUpdateDTO;
import ru.hits.messengerapi.notifications.service.NotificationService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount() {
        return new ResponseEntity<>(notificationService.getUnreadCount(), HttpStatus.OK);
    }

    @PutMapping("/mark-as-read-or-unread")
    public ResponseEntity<Long> markAsReadOrUnread(@RequestBody @Valid
                                               NotificationsStatusUpdateDTO notificationsStatusUpdateDTO) {
        return new ResponseEntity<>(notificationService.markNotificationsAsReadOrUnread(notificationsStatusUpdateDTO),
                HttpStatus.OK);
    }

}
