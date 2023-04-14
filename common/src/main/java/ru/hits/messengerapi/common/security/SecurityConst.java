package ru.hits.messengerapi.common.security;

import lombok.experimental.UtilityClass;

/**
 * Класс-утилита, содержащий значения заголовков запросов, связанных с безопасностью приложения.
 */
@UtilityClass
public class SecurityConst {
    public static final String HEADER_JWT = "Authorization";
    public static final String HEADER_API_KEY = "API_KEY";

}
