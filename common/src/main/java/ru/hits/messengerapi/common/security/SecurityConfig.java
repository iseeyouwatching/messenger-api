package ru.hits.messengerapi.common.security;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.hits.messengerapi.common.security.props.SecurityProps;

import java.util.Objects;

/**
 * Конфигурация безопасности приложения
 * Данный класс определяет настройки авторизации и аутентификации, а также включает фильтр JWT.
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTFilter jwtFilter;
    private final SecurityProps securityProps;

    /**
     * Метод, который настраивает правила безопасности и включает фильтр JWT.
     *
     * @param http объект класса {@link HttpSecurity} для настройки правил безопасности.
     */
    @SneakyThrows
    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) {
        http = http.requestMatcher(request -> Objects.nonNull(request.getServletPath())
                        && request.getServletPath().startsWith(securityProps.getJwtToken().getRootPath()))
                .authorizeRequests()
                .antMatchers(securityProps.getJwtToken().getPermitAll()).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class
                );
        return finalize(http);
    }

    /**
     * Настройка для интеграции между сервисами
     */
    @SneakyThrows
    @Bean
    public SecurityFilterChain filterChainIntegration(HttpSecurity http) {
        http = http.requestMatcher(request -> Objects.nonNull(request.getServletPath())
                        && request.getServletPath().startsWith(securityProps.getIntegrations().getRootPath()))
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(
                        new ApiIntegrationFilter(securityProps.getIntegrations().getApiKey()),
                        UsernamePasswordAuthenticationFilter.class
                );
        return finalize(http);
    }

    @SneakyThrows
    @Bean
    public SecurityFilterChain filterChainDenyAll(HttpSecurity http) {
        http = http.requestMatcher(request -> Objects.nonNull(request.getServletPath())
                        && !request.getServletPath().startsWith(securityProps.getJwtToken().getRootPath())
                        && !request.getServletPath().startsWith(securityProps.getIntegrations().getRootPath()))
                .authorizeRequests()
                .anyRequest().authenticated()
                .and();
        return finalize(http);
    }

    @SneakyThrows
    private SecurityFilterChain finalize(HttpSecurity http) {
        return http.csrf()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .build();
    }

}
