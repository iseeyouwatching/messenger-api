package ru.hits.messengerapi.common.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * Класс, представляющий аутентификацию при интеграционных запросах.
 */
public class IntegrationAuthentication extends AbstractAuthenticationToken {

    /**
     * Конструктор, в котором создается новый экземпляр класса {@link IntegrationAuthentication}
     * и устанавливается аутентификация.
     */
    public IntegrationAuthentication() {
        super(null);
        setAuthenticated(true);
    }

    /**
     * Возвращает данные, необходимые для аутентификации. В данном случае - null.
     */
    @Override
    public Object getCredentials() {
        return null;
    }

    /**
     * Возвращает данные об аутентифицированном пользователе. В данном случае - null.
     */
    @Override
    public Object getPrincipal() {
        return null;
    }
}
