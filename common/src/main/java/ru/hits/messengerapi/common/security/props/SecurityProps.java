package ru.hits.messengerapi.common.security.props;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Класс, содержащий свойства безопасности приложения.
 */
@ConfigurationProperties("app.security")
@Getter
@Setter
@ToString
public class SecurityProps {

    /**
     * Свойства JWT-токена системы безопасности.
     */
    private SecurityJwtTokenProps jwtToken;

    /**
     * Свойства интеграционных запросов.
     */
    private SecurityIntegrationsProps integrations;

}
