package ru.hits.messengerapi.user.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.hits.messengerapi.common.security.props.SecurityProps;
import ru.hits.messengerapi.user.service.IntegrationRequestsServiceInterface;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static ru.hits.messengerapi.common.security.SecurityConst.HEADER_API_KEY;

/**
 * Сервис для интеграционных запросов.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IntegrationRequestsService implements IntegrationRequestsServiceInterface {

    /**
     * Свойства безопасности приложения.
     */
    private final SecurityProps securityProps;

    /**
     * URL, куда отправляет интеграционный запрос на проверку существования пользователя.
     */
    @Value("${integration.request.check-existence-in-blacklist}")
    private String integrationUsersRequestCheckExistenceInBlacklist;

    /**
     * Метод для проверки нахождения пользователя в черном списке другого пользователя.
     *
     * @param targetUserId ID целевого пользователя.
     * @param blockedUserId ID заблокированного пользователя.
     * @return true - пользователь находится в черном списке другого пользователя, false - не находится.
     */
    @Override
    public Boolean checkExistenceInBlacklist(UUID targetUserId, UUID blockedUserId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HEADER_API_KEY, securityProps.getIntegrations().getApiKey());

        Map<String, UUID> idMap = new HashMap<>();
        idMap.put("targetUserId", targetUserId);
        idMap.put("blockedUserId", blockedUserId);

        HttpEntity<Map<String, UUID>> requestEntity = new HttpEntity<>(idMap, headers);

        RestTemplate restTemplate = new RestTemplate();
        String url = integrationUsersRequestCheckExistenceInBlacklist;
        ResponseEntity<Boolean> responseEntity = restTemplate
                .exchange(url, HttpMethod.POST, requestEntity, Boolean.class);

        Boolean isBlocked = responseEntity.getBody();

        String logMessage = isBlocked ? "заблокирован" : "не заблокирован";
        log.info("Пользователь с id {} {} пользователем с id {}.", targetUserId, logMessage, blockedUserId);

        return isBlocked;
    }
}
