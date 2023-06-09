package ru.hits.messengerapi.filestorage.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Класс конфигурации Swagger.
 */
@Configuration
@OpenAPIDefinition(info = @Info(title = "Сервис файлового хранилища", version = "v1"))
public class SwaggerConfig {
}
