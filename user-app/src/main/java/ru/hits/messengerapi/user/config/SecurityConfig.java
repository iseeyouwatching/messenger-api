package ru.hits.messengerapi.user.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.hits.messengerapi.user.security.CustomUserDetailsService;

/**
 * Конфигурация безопасности приложения
 * Данный класс определяет настройки авторизации и аутентификации, а также включает фильтр JWT.
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JWTFilter jwtFilter;

    /**
     * Метод, который настраивает правила безопасности и включает фильтр JWT.
     *
     * @param http объект класса {@link HttpSecurity} для настройки правил безопасности.
     * @throws Exception в случае ошибок при настройке правил безопасности.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.PUT, "/api/users").authenticated()
                .antMatchers(HttpMethod.GET, "/api/users/**").authenticated()
                .anyRequest().permitAll()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

    /**
     * Метод, который настраивает менеджер аутентификации для использования сервиса пользователей
     * и парольного кодировщика.
     *
     * @param auth объект класса {@link AuthenticationManagerBuilder} для настройки менеджера аутентификации.
     * @throws Exception в случае ошибок при настройке менеджера аутентификации.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService)
                .passwordEncoder(getPasswordEncoder());
    }

    /**
     * Метод, который создает бин для использования {@link BCryptPasswordEncoder} при
     * кодировании пароля пользователей.
     *
     * @return объект класса {@link BCryptPasswordEncoder} для использования при кодировании пароля пользователей.
     */
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
