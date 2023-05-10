package ru.hits.messengerapi.notifications.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hits.messengerapi.notifications.dto.NotificationsPageListDto;
import ru.hits.messengerapi.notifications.dto.NotificationsStatusUpdateDTO;
import ru.hits.messengerapi.notifications.dto.PaginationAndFiltersDto;
import ru.hits.messengerapi.notifications.service.NotificationService;

import javax.validation.Valid;

/**
 * Контроллер для работы с уведомлениями.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Уведомления.")
public class NotificationController {

    /**
     * Сервис уведомлений.
     */
    private final NotificationService notificationService;

    /**
     * Метод, который возвращает список уведомлений с заданными параметрами пагинации и фильтрации.
     *
     * @param paginationAndFiltersDto объект, содержащий параметры пагинации и фильтрации.
     * @return объект ResponseEntity, содержащий список уведомлений и статус ответа.
     */
    @Operation(
            summary = "Получить список уведомлений.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/get")
    public ResponseEntity<NotificationsPageListDto> getNotifications(@RequestBody @Valid PaginationAndFiltersDto
                                                                                 paginationAndFiltersDto) {
        return new ResponseEntity<>(notificationService.getNotifications(paginationAndFiltersDto), HttpStatus.OK);
    }

    /**
     * Метод, который возвращает количество непрочитанных уведомлений для текущего пользователя.
     *
     * @return объект ResponseEntity, содержащий количество непрочитанных уведомлений и статус ответа.
     */
    @Operation(
            summary = "Получить количество непрочитанных сообщений.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount() {
        return new ResponseEntity<>(notificationService.getUnreadCount(), HttpStatus.OK);
    }

    /**
     * Метод, который помечает уведомления как прочитанные или непрочитанные в соответствии
     * с переданными параметрами.
     *
     * @param notificationsStatusUpdateDTO объект, содержащий идентификаторы уведомлений и их статус.
     * @return объект ResponseEntity, содержащий количество обновленных уведомлений и статус ответа.
     */
    @Operation(
            summary = "Пометить уведомления как прочитанные/непрочитанные.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/mark-as-read-or-unread")
    public ResponseEntity<Long> markAsReadOrUnread(@RequestBody @Valid
                                               NotificationsStatusUpdateDTO notificationsStatusUpdateDTO) {
        return new ResponseEntity<>(notificationService
                .markNotificationsAsReadOrUnread(notificationsStatusUpdateDTO),
                HttpStatus.OK);
    }

}
