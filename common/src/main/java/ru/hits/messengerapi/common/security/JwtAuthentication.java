package ru.hits.messengerapi.common.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * Класс, представляющий аутентификацию по JWT-токену.
 */
public class JwtAuthentication extends AbstractAuthenticationToken {

    /**
     * Конструктор, в котором создается новый экземпляр класса {@link JwtAuthentication}
     * и устанавливается аутентификация.
     *
     * @param jwtUserData данные, которые можно достать из аутентификации.
     */
    public JwtAuthentication(JwtUserData jwtUserData) {
        super(null);
        this.setDetails(jwtUserData);
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
     * Возвращает данные об аутентифицированном пользователе.
     *
     * @return данные об аутентифицированном пользователе.
     */
    @Override
    public Object getPrincipal() {
        return getDetails();
    }

}
