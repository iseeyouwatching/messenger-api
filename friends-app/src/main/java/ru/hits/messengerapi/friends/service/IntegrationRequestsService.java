package ru.hits.messengerapi.friends.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ru.hits.messengerapi.common.controller.RestTemplateErrorHandler;
import ru.hits.messengerapi.common.dto.UserIdAndFullNameDto;
import ru.hits.messengerapi.common.security.props.SecurityProps;
import ru.hits.messengerapi.friends.dto.common.AddPersonDto;
import ru.hits.messengerapi.friends.entity.BlacklistEntity;
import ru.hits.messengerapi.friends.entity.FriendEntity;
import ru.hits.messengerapi.friends.repository.BlacklistRepository;
import ru.hits.messengerapi.friends.repository.FriendsRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static ru.hits.messengerapi.common.security.SecurityConst.HEADER_API_KEY;

/**
 * Сервис для интеграционных запросов.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IntegrationRequestsService {

    /**
     * Репозиторий для работы с сущностью {@link BlacklistEntity}.
     */
    private final BlacklistRepository blacklistRepository;

    /**
     * Репозиторий для работы с сущностью {@link FriendEntity}.
     */
    private final FriendsRepository friendsRepository;

    /**
     * Свойства безопасности приложения.
     */
    private final SecurityProps securityProps;

    /**
     * URL, куда отправляет интеграционный запрос на проверку существования пользователя.
     */
    @Value("${integration.request.check-existence}")
    private String integrationUsersRequestCheckExistence;

    /**
     * Метод для проверки существования пользователя по ФИО и ID.
     *
     * @param addPersonDto информация проверяемого пользователя.
     */
    public void checkUserExistence(AddPersonDto addPersonDto) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new RestTemplateErrorHandler(new ObjectMapper()));
        String url = integrationUsersRequestCheckExistence;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HEADER_API_KEY, securityProps.getIntegrations().getApiKey());
        HttpEntity<AddPersonDto> requestEntity = new HttpEntity<>(addPersonDto, headers);

        ResponseEntity<Boolean> responseEntity = restTemplate
                .exchange(url, HttpMethod.POST, requestEntity, Boolean.class);

        log.info("Пользователь с id {} и ФИО {} существует.", addPersonDto.getId(), addPersonDto.getFullName());
    }

    /**
     * Синхронизация данных заблокированного пользователя.
     *
     * @param userIdAndFullNameDto DTO с информацией о ID и ФИО пользователя.
     */
    @Transactional
    public void syncBlockedUserData(UserIdAndFullNameDto userIdAndFullNameDto) {
        UUID id = userIdAndFullNameDto.getId();
        String fullName = userIdAndFullNameDto.getFullName();
        log.debug("Получено полное имя {} для пользователя с ID {}", fullName, id);

        List<BlacklistEntity> blockedUsers = blacklistRepository.findAllByBlockedUserId(id);

        blockedUsers.forEach(user -> user.setBlockedUserName(fullName));
        blacklistRepository.saveAll(blockedUsers);

        log.info("Данные заблокированного пользователя с ID {} были успешно синхронизированы.", id);
    }

    /**
     * Синхронизация данных друга.
     *
     * @param userIdAndFullNameDto DTO с информацией о ID и ФИО пользователя.
     */
    @Transactional
    public void syncFriendData(UserIdAndFullNameDto userIdAndFullNameDto) {
        UUID id = userIdAndFullNameDto.getId();
        String fullName = userIdAndFullNameDto.getFullName();
        log.debug("Получено полное имя {} для пользователя с ID {}", fullName, id);

        List<FriendEntity> friends = friendsRepository.findAllByAddedUserId(id);

        friends.forEach(user -> user.setFriendName(fullName));
        friendsRepository.saveAll(friends);

        log.info("Синхронизация данных для пользователя с ID {} завершена.", id);
    }

}
