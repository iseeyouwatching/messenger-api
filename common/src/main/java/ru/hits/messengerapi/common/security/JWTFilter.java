package ru.hits.messengerapi.common.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.hits.messengerapi.common.JWTUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

import static ru.hits.messengerapi.common.security.SecurityConst.HEADER_JWT;

/**
 * Фильтр для обработки JWT-токена, полученного из заголовка "Authorization".
 * Проверяет валидность токена и устанавливает аутентификацию пользователя.
 */
@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    /**
     * Метод для обработки каждого запроса и проверки валидности JWT-токена.
     * Если токен валидный, то устанавливает аутентификацию пользователя.
     *
     * @param httpServletRequest HTTP-запрос.
     * @param httpServletResponse HTTP-ответ.
     * @param filterChain цепочка фильтров.
     * @throws ServletException в случае ошибки при обработке запроса.
     * @throws IOException в случае ошибки ввода-вывода.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = httpServletRequest.getHeader(HEADER_JWT);

        if (authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            if (jwt.isBlank()) {
                httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            } else {
                try {
                    UUID id = UUID.fromString(jwtUtil.validateTokenAndRetrieveClaim(jwt).get(0));
                    String login = jwtUtil.validateTokenAndRetrieveClaim(jwt).get(1);
                    var userData = new JwtUserData(login, id);
                    var authentication = new JwtAuthentication(userData);

                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } catch (JWTVerificationException exception) {
                    httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                }
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}
