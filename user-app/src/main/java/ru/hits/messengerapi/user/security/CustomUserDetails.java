package ru.hits.messengerapi.user.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.hits.messengerapi.user.entity.UserEntity;

import java.util.Collection;

/**
 * Класс, который представляет пользователя, аутентифицированного в системе,
 * с помощью Spring Security. Он реализует интерфейс {@link UserDetails},
 * который содержит основные методы для работы с данными пользователя.
 */
public class CustomUserDetails implements UserDetails {
    private final UserEntity user;

    /**
     * Конструктор класса, который принимает объект {@link UserEntity}
     * в качестве аргумента и инициализирует поле user.
     *
     * @param user объект класса {@link UserEntity}.
     */
    public CustomUserDetails(UserEntity user) {
        this.user = user;
    }

    /**
     * Метод для получения списка прав доступа пользователя.
     *
     * @return null
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    /**
     * Метод для получения пароля пользователя.
     *
     * @return пароль пользователя.
     */
    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    /**
     * Метод для получения имени пользователя (в данном случае ID пользователя в виде строки).
     *
     * @return ID пользователя.
     */
    @Override
    public String getUsername() {
        return this.user.getId().toString();
    }

    /**
     * Метод для проверки того, не истек ли срок действия учетной записи пользователя.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Метод для проверки того, не заблокирован ли пользователь.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Метод для проверки того, не истек ли срок действия учетных данных пользователя.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Метод для проверки того, активен ли пользователь.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Метод для получения данных аутентифицированного пользователя.
     *
     * @return объект класса {@link UserEntity} с данными пользователя.
     */
    public UserEntity getUser() {
        return this.user;
    }
}
