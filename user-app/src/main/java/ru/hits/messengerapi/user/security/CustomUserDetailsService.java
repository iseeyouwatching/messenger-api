package ru.hits.messengerapi.user.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.hits.messengerapi.user.entity.UserEntity;
import ru.hits.messengerapi.user.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Сервис, который используется Spring Security для аутентификации пользователя.
 * Он реализует интерфейс {@link UserDetailsService}, который содержит основные методы
 * для работы с данными пользователя.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * Метод, который получает пользователя из базы данных по его ID,
     * и возвращает объект {@link CustomUserDetails} на основе полученных данных.
     *
     * @param id ID пользователя.
     * @return объект класса {@link CustomUserDetails}, представляющий пользователя.
     * @throws UsernameNotFoundException если пользователь не найден.
     */
    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Optional<UserEntity> user = userRepository.findById(UUID.fromString(id));

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Пользователь с ID " + id + " не найден.");
        }

        return new CustomUserDetails(user.get());
    }
}
