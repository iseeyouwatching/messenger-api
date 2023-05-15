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
     * URL, куда отправляется запрос на проверку существования списка идентификаторов вложений.
     */
    @Value("${integration.request.check-multi-attachment-existence}")
    private String integrationFilesRequestCheckMultiAttachmentExistence;

    /**
     * URL, куда отправляется запрос на проверку существования файла (аватарки) по идентификатору.
     */
    @Value("${integration.request.check-avatar-id-existence}")
    private String integrationFileStorageRequestCheckAvatarIdExistence;

    /**
     * URL, куда отправляется запрос на получение названия файла по идентификатору.
     */
    @Value("${integration.request.get-filename}")
    private String integrationFileGetFilename;

    /**
     * URL, куда отправляется запрос на получение размера файла по идентификатору.
     */
    @Value("${integration.request.get-file-size}")
    private String integrationFileGetFileSize;


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

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            log.info("Запрос на проверку существования пользователя с ID {} выполнен успешно.", id);
        } else {
            log.warn("Запрос на проверку существования пользователя с ID {} завершился с ошибкой. Код ошибки: {}.",
                    id, responseEntity.getStatusCodeValue());
        }
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

        List<String> result = responseEntity.getBody();
        if (result != null) {
            log.debug("Получены данные о полном имени и ID аватара пользователя с ID {}", id);
        } else {
            log.warn("Не удалось получить данные о полном имени и ID аватара пользователя с ID {}", id);
        }

        return result;
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

        log.debug("Вызов метода checkExistenceInFriends с параметрами authUserId={}, receiverId={}", authUserId, receiverId);

        ResponseEntity<Boolean> responseEntity = restTemplate
                .exchange(url, HttpMethod.POST, requestEntity, Boolean.class);

        log.debug("Ответ от метода checkExistenceInFriends: {}", responseEntity.getBody());
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

        if (responseEntity != null && responseEntity.getBody() != null) {
            log.info("Проверка наличия нескольких пользователей в друзьях прошла успешно. Результат: {}",
                    responseEntity.getBody());
        } else {
            log.warn("Проверка наличия нескольких пользователей в друзьях завершилась с ошибкой. Результат: null");
        }
    }

    /**
     * Метод для проверки существования списка идентификаторов вложений.
     *
     * @param attachmentsIds список идентификаторов вложений.
     */
    public void checkMultiAttachmentExistence(List<UUID> attachmentsIds) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new RestTemplateErrorHandler(new ObjectMapper()));
        String url = integrationFilesRequestCheckMultiAttachmentExistence;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HEADER_API_KEY, securityProps.getIntegrations().getApiKey());
        HttpEntity<List<UUID>> requestEntity = new HttpEntity<>(attachmentsIds, headers);

        ResponseEntity<Void> responseEntity = restTemplate
                .exchange(url, HttpMethod.POST, requestEntity, Void.class);
    }

    /**
     * Мето для проверки существования файла (аватарки) с переданным идентификатором.
     *
     * @param id идентификатор файла.
     */
    public void checkAvatarIdExistence(UUID id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HEADER_API_KEY, securityProps.getIntegrations().getApiKey());

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new RestTemplateErrorHandler(new ObjectMapper()));
        String url = integrationFileStorageRequestCheckAvatarIdExistence;

        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), Void.class, id);
    }

    /**
     * Метод для получения названия файла по его идентификатору.
     *
     * @param id идентификатор файла.
     * @return название файла.
     */
    public String getFilename(UUID id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HEADER_API_KEY, securityProps.getIntegrations().getApiKey());

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new RestTemplateErrorHandler(new ObjectMapper()));
        String url = integrationFileGetFilename;

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers),String.class, id);
        return response.getBody();
    }

    /**
     * Метод для получения размера файла по его идентификатору.
     *
     * @param id идентификатор файла.
     * @return размер файла.
     */
    public String getFileSize(UUID id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HEADER_API_KEY, securityProps.getIntegrations().getApiKey());

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new RestTemplateErrorHandler(new ObjectMapper()));
        String url = integrationFileGetFileSize;

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers),String.class, id);
        return response.getBody();
    }

}
