package ru.hits.messengerapi.user.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Класс конфигурации Swagger.
 */
@Configuration
@OpenAPIDefinition(info = @Info(title = "Сервис аутентификации и профилей", version = "v1"))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "Bearer"
)
//@SecurityScheme(
//        name = "api_key",
//        type = SecuritySchemeType.APIKEY,
//        in = SecuritySchemeIn.HEADER,
//        paramName = "API_KEY"
//)
public class SwaggerConfig {
}
