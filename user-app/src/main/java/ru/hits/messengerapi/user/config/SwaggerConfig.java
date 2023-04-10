package ru.hits.messengerapi.user.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Класс конфигурации Swagger для API-интерфейса.
 * Swagger - это инструмент для создания документации по API.
 * Этот класс содержит конфигурацию Swagger, которая устанавливает название и версию API,
 * а также определяет тип авторизации (Bearer token) для доступа к документации.
 */
@Configuration
@OpenAPIDefinition(info = @Info(title = "messenger-api", version = "v1"))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "Bearer"
)
public class SwaggerConfig {
}
