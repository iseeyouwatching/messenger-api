package ru.hits.messengerapi.notifications.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.messengerapi.common.dto.NewNotificationDto;
import ru.hits.messengerapi.common.dto.PageInfoDto;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.common.helpingservices.CheckPaginationInfoService;
import ru.hits.messengerapi.common.security.JwtUserData;
import ru.hits.messengerapi.notifications.dto.NotificationDto;
import ru.hits.messengerapi.notifications.dto.NotificationsPageListDto;
import ru.hits.messengerapi.notifications.dto.NotificationsStatusUpdateDTO;
import ru.hits.messengerapi.notifications.dto.PaginationAndFiltersDto;
import ru.hits.messengerapi.notifications.entity.NotificationEntity;
import ru.hits.messengerapi.notifications.enumeration.NotificationStatus;
import ru.hits.messengerapi.notifications.repository.NotificationRepository;
import ru.hits.messengerapi.notifications.specification.NotificationSpec;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * Вспомогательный сервис для проверки данных для пагинации.
     */
    private final CheckPaginationInfoService checkPaginationInfoService;

    @Transactional
    public void getNotification(NewNotificationDto newNotificationDto) {
        NotificationEntity notification = NotificationEntity.builder()
                .type(newNotificationDto.getType())
                .text(newNotificationDto.getText())
                .userId(newNotificationDto.getUserId())
                .status(NotificationStatus.UNREAD)
                .receiveTime(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
    }

    public Long getUnreadCount() {
        return notificationRepository.countByUserIdAndStatus(getAuthenticatedUserId(), NotificationStatus.UNREAD);
    }

    @Transactional
    public Long markNotificationsAsReadOrUnread(NotificationsStatusUpdateDTO notificationsStatusUpdateDTO) {
        List<UUID> invalidIds = new ArrayList<>();
        for (UUID id: notificationsStatusUpdateDTO.getNotificationsIDs()) {
            if (notificationRepository.findById(id).isEmpty()) {
                invalidIds.add(id);
            }
        }

        if (!invalidIds.isEmpty()) {
            throw new NotFoundException("Уведомления с ID " + invalidIds + " не существуют.");
        }

        if (notificationsStatusUpdateDTO.getStatus().equals(NotificationStatus.READ)) {
            notificationRepository.markAsRead(notificationsStatusUpdateDTO.getNotificationsIDs(),
                    notificationsStatusUpdateDTO.getStatus(), LocalDateTime.now());
        } else if (notificationsStatusUpdateDTO.getStatus().equals(NotificationStatus.UNREAD)) {
            notificationRepository.markAsUnread(notificationsStatusUpdateDTO.getNotificationsIDs(),
                    notificationsStatusUpdateDTO.getStatus(), null);
        }
        return getUnreadCount();
    }

    public NotificationsPageListDto getNotifications(PaginationAndFiltersDto paginationAndFiltersDto) {
        int pageNumber = paginationAndFiltersDto.getPageInfo() != null &&
                paginationAndFiltersDto.getPageInfo().getPageNumber() == null ? 1
                : paginationAndFiltersDto.getPageInfo().getPageNumber();
        int pageSize = paginationAndFiltersDto.getPageInfo() != null &&
                paginationAndFiltersDto.getPageInfo().getPageSize() == null ? 10
                : paginationAndFiltersDto.getPageInfo().getPageSize();
        checkPaginationInfoService.checkPagination(pageNumber, pageSize);
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize,
                Sort.by(Sort.Direction.DESC, "receiveTime"));

        UUID authenticatedUserId = getAuthenticatedUserId();

        Specification<NotificationEntity> spec = Specification
                .where(NotificationSpec.userIdEqual(authenticatedUserId));
        if (paginationAndFiltersDto.getFilters() != null) {
            if (paginationAndFiltersDto.getFilters().getPeriodFilter().getFromDateTime() != null) {
                spec = spec.and(NotificationSpec
                        .receivedDateAfter(paginationAndFiltersDto.getFilters().getPeriodFilter()
                                .getFromDateTime()));
            }
            if (paginationAndFiltersDto.getFilters().getPeriodFilter().getToDateTime() != null) {
                spec = spec.and(NotificationSpec
                        .receivedDateBefore(paginationAndFiltersDto.getFilters().getPeriodFilter()
                                .getToDateTime()));
            }
            if (paginationAndFiltersDto.getFilters().getTextFilter() != null) {
                spec = spec.and(NotificationSpec.textLike(paginationAndFiltersDto.getFilters().getTextFilter()));
            }
            if (paginationAndFiltersDto.getFilters().getTypes() != null
                    && !paginationAndFiltersDto.getFilters().getTypes().isEmpty()) {
                spec = spec.and(NotificationSpec.typeIn(paginationAndFiltersDto.getFilters().getTypes()));
            }
        }

        Page<NotificationEntity> notificationsPage = notificationRepository.findAll(spec, pageable);

        List<NotificationDto> notificationDtos = notificationsPage.getContent().stream()
                .map(NotificationDto::from)
                .collect(Collectors.toList());

        NotificationsPageListDto result = new NotificationsPageListDto();
        result.setNotifications(notificationDtos);
        result.setPageInfo(new PageInfoDto(notificationsPage.getPageable().getPageNumber() + 1,
                notificationsPage.getPageable().getPageSize()));
        result.setFilters(paginationAndFiltersDto.getFilters());

        return result;
    }

    /**
     * Метод для получения ID аутентифицированного пользователя.
     *
     * @return ID аутентифицированного пользователя.
     */
    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
        return userData.getId();
    }

}
