package ru.hits.messengerapi.friends.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.common.security.props.SecurityProps;
import ru.hits.messengerapi.friends.dto.AddPersonDto;
import ru.hits.messengerapi.friends.service.IntegrationRequestsServiceInterface;

import java.util.Objects;
import java.util.UUID;

import static ru.hits.messengerapi.common.security.SecurityConst.HEADER_API_KEY;

@Service
@RequiredArgsConstructor
public class IntegrationRequestsService implements IntegrationRequestsServiceInterface {

    private final SecurityProps securityProps;

    @Override
    public void checkUserExistence(AddPersonDto addPersonDto) {
        RestTemplate restTemplate = new RestTemplate();
        String url =
                "http://localhost:8191/integration/users/check-existence";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HEADER_API_KEY, securityProps.getIntegrations().getApiKey());
        HttpEntity<AddPersonDto> requestEntity = new HttpEntity<>(addPersonDto, headers);

        ResponseEntity<String> responseEntity = restTemplate
                .exchange(url, HttpMethod.POST, requestEntity, String.class);

        if (Objects.equals(responseEntity.getBody(), "dont exist")) {
            throw new NotFoundException("Пользователя с id " + addPersonDto.getId()
                    + " и ФИО " + addPersonDto.getFullName() + " не существует.");
        }
    }

    @Override
    public String getFullName(UUID id) {
        RestTemplate restTemplate = new RestTemplate();
        String url =
                "http://localhost:8191/integration/users/get-full-name";

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
