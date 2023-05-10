package ru.hits.messengerapi.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ru.hits.messengerapi.chat.entity.MessageEntity;
import ru.hits.messengerapi.chat.repository.MessageRepository;
import ru.hits.messengerapi.common.controller.RestTemplateErrorHandler;
import ru.hits.messengerapi.common.security.props.SecurityProps;

import java.util.*;

import static ru.hits.messengerapi.common.security.SecurityConst.HEADER_API_KEY;

/**
 * Сервис интеграционных запросов.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IntegrationRequestsService {

    /**
     * Репозиторий для работы с сущностью {@link MessageEntity}.
     */
    private final MessageRepository messageRepository;

    /**
     * Свойства безопасности приложения.
     */
    private final SecurityProps securityProps;

    /**
     * URL, куда отправляется запрос на проверку существования пользователя по его ID.
     */
    @Value("${integration.request.check-existence-by-id}")
    private String integrationUsersRequestCheckExistenceById;

    /**
     * URL, куда отправляется запрос на проверку существования пользователя в друзьях.
     */
    @Value("${integration.request.check-existence-in-friends}")
    private String integrationUsersRequestCheckExistenceInFriends;

    /**
     * URL, куда отправляется запрос на проверку существования пользователей в друзьях.
     */
    @Value("${integration.request.check-multi-existence-in-friends}")
    private String integrationUsersRequestCheckMultiExistenceInFriends;

    /**
     * URL, куда отправляется запрос на получение ФИО и аватарки.
     */
    @Value("${integration.request.get-full-name-and-avatar}")
    private String integrationUsersRequestGetFullNameAndAvatar;

    /**
     * Метод для проверки существования пользователя по его ID.
     *
     * @param id идентификатор пользователя.
     */
    public void checkUserExistence(UUID id) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new RestTemplateErrorHandler(new ObjectMapper()));
        String url = integrationUsersRequestCheckExistenceById;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HEADER_API_KEY, securityProps.getIntegrations().getApiKey());
        HttpEntity<UUID> requestEntity = new HttpEntity<>(id, headers);

        ResponseEntity<Boolean> responseEntity = restTemplate
                .exchange(url, HttpMethod.POST, requestEntity, Boolean.class);
    }

    /**
     * Метод для получения ФИО и аватарки пользователя по его ID.
     *
     * @param id идентификатор пользователя.
     * @return ФИО и аватарка пользователя.
     */
    public List<String> getFullNameAndAvatarId(UUID id) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new RestTemplateErrorHandler(new ObjectMapper()));
        String url = integrationUsersRequestGetFullNameAndAvatar;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HEADER_API_KEY, securityProps.getIntegrations().getApiKey());
        HttpEntity<UUID> requestEntity = new HttpEntity<>(id, headers);

        ResponseEntity<List<String>> responseEntity = restTemplate
                .exchange(url, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {
                });

        return responseEntity.getBody();
    }

    /**
     * Метод для синхронизации данных пользователя.
     *
     * @param id идентификатор пользователя, чьи данные необходимо синхронизировать.
     * @return сообщение об успешной синхронизации.
     */
    @Transactional
    public Map<String, String> syncUserData(UUID id) {
        List<String> fullNameAndAvatarId = getFullNameAndAvatarId(id);
        String fullName = fullNameAndAvatarId.get(0);
        String avatarIdString = fullNameAndAvatarId.get(1);
        UUID senderAvatarId = null;
        if (avatarIdString != null) {
            senderAvatarId = UUID.fromString(avatarIdString);
        }

        List<MessageEntity> messageEntities = messageRepository.findAllBySenderId(id);

        for (MessageEntity message: messageEntities) {
            message.setSenderName(fullName);
            message.setSenderAvatarId(senderAvatarId);
        }
        messageRepository.saveAll(messageEntities);

        return Map.of("message", "Синхронизация данных прошла успешно.");
    }

    /**
     * Метод для проверки существования пользователя в друзьях у другого пользователя.
     *
     * @param authUserId идентификатор аутентифицированного пользователя.
     * @param receiverId идентификатор пользователя, которого мы проверяем.
     */
    public void checkExistenceInFriends(UUID authUserId, UUID receiverId) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new RestTemplateErrorHandler(new ObjectMapper()));
        String url = integrationUsersRequestCheckExistenceInFriends;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HEADER_API_KEY, securityProps.getIntegrations().getApiKey());
        List<UUID> uuids = new ArrayList<>();
        uuids.add(authUserId);
        uuids.add(receiverId);
        HttpEntity<List<UUID>> requestEntity = new HttpEntity<>(uuids, headers);

        ResponseEntity<Boolean> responseEntity = restTemplate
                .exchange(url, HttpMethod.POST, requestEntity, Boolean.class);
    }

    /**
     * Метод для проверки существования пользователей в друзьях у другого пользователя.
     *
     * @param authUserId идентификатор аутентифицированного пользователя.
     * @param multiUsersIds идентификаторы пользователей, которых мы проверяем.
     */
    public void checkExistenceMultiUsersInFriends(UUID authUserId, List<UUID> multiUsersIds) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new RestTemplateErrorHandler(new ObjectMapper()));
        String url = integrationUsersRequestCheckMultiExistenceInFriends;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HEADER_API_KEY, securityProps.getIntegrations().getApiKey());
        List<UUID> uuids = new ArrayList<>();
        uuids.add(authUserId);
        uuids.addAll(multiUsersIds);
        HttpEntity<List<UUID>> requestEntity = new HttpEntity<>(uuids, headers);

        ResponseEntity<Boolean> responseEntity = restTemplate
                .exchange(url, HttpMethod.POST, requestEntity, Boolean.class);
    }

}
