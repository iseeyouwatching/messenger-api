package ru.hits.messengerapi.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.hits.messengerapi.chat.entity.MessageEntity;
import ru.hits.messengerapi.chat.repository.MessageRepository;
import ru.hits.messengerapi.common.controller.RestTemplateErrorHandler;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.common.security.props.SecurityProps;

import java.util.*;

import static ru.hits.messengerapi.common.security.SecurityConst.HEADER_API_KEY;

@Service
@RequiredArgsConstructor
@Slf4j
public class IntegrationRequestsService {
    private final MessageRepository messageRepository;

    /**
     * Свойства безопасности приложения.
     */
    private final SecurityProps securityProps;

    @Value("${integration.request.check-existence-by-id}")
    private String integrationUsersRequestCheckExistenceById;

    @Value("${integration.request.check-existence-in-friends}")
    private String integrationUsersRequestCheckExistenceInFriends;

    @Value("${integration.request.check-multi-existence-in-friends}")
    private String integrationUsersRequestCheckMultiExistenceInFriends;

    @Value("${integration.request.get-full-name-and-avatar}")
    private String integrationUsersRequestGetFullNameAndAvatar;

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
     * Метод для получения ФИО пользователя по его ID.
     *
     * @param id идентификатор пользователя.
     * @return ФИО пользователя.
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
