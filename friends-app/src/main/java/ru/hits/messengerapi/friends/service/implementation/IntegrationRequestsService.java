package ru.hits.messengerapi.friends.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.common.security.props.SecurityProps;
import ru.hits.messengerapi.friends.dto.common.AddPersonDto;
import ru.hits.messengerapi.friends.service.IntegrationRequestsServiceInterface;

import java.util.Objects;
import java.util.UUID;

import static ru.hits.messengerapi.common.security.SecurityConst.HEADER_API_KEY;

/**
 * Сервис для интеграционных запросов.
 */
@Service
@RequiredArgsConstructor
public class IntegrationRequestsService implements IntegrationRequestsServiceInterface {

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
     * URL, куда отправляет интеграционный запрос для получения ФИО пользователя.
     */
    @Value("${integration.request.get-full-name}")
    private String integrationUsersRequestGetFullName;

    /**
     * Метод для проверки существования пользователя по ФИО и ID.
     *
     * @param addPersonDto информация проверяемого пользователя.
     */
    @Override
    public void checkUserExistence(AddPersonDto addPersonDto) {
        RestTemplate restTemplate = new RestTemplate();
        String url = integrationUsersRequestCheckExistence;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HEADER_API_KEY, securityProps.getIntegrations().getApiKey());
        HttpEntity<AddPersonDto> requestEntity = new HttpEntity<>(addPersonDto, headers);

        ResponseEntity<Boolean> responseEntity = restTemplate
                .exchange(url, HttpMethod.POST, requestEntity, Boolean.class);

        if (Boolean.FALSE.equals(responseEntity.getBody())) {
            throw new NotFoundException("Пользователя с id " + addPersonDto.getId()
                    + " и ФИО " + addPersonDto.getFullName() + " не существует.");
        }
    }

    /**
     * Метод для получения ФИО пользователя по его ID.
     *
     * @param id идентификатор пользователя.
     * @return ФИО пользователя.
     */
    @Override
    public String getFullName(UUID id) {
        RestTemplate restTemplate = new RestTemplate();
        String url = integrationUsersRequestGetFullName;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HEADER_API_KEY, securityProps.getIntegrations().getApiKey());
        HttpEntity<UUID> requestEntity = new HttpEntity<>(id, headers);

        ResponseEntity<String> responseEntity = restTemplate
                .exchange(url, HttpMethod.POST, requestEntity, String.class);

        if (Objects.equals(responseEntity.getBody(), "dont exist")) {
            throw new NotFoundException("Пользователя с id " + id + " не существует.");
        }

        return responseEntity.getBody();
    }

}
