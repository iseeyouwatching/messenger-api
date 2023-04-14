package ru.hits.messengerapi.common.security.props;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Класс, содержащий свойства необходимые для интеграционных запросов.
 */
@Getter
@Setter
@ToString
public class SecurityIntegrationsProps {

    /**
     * API-ключ для доступа к системе безопасности.
     */
    private String apiKey;

    /**
     * Корневой путь интеграционных запросов.
     */
    private String rootPath;

}
