package ru.hits.messengerapi.user.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Фильтр для обработки JWT-токена, полученного из заголовка "Authorization".
 * Проверяет валидность токена и устанавливает аутентификацию пользователя.
 */
@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

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
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = httpServletRequest.getHeader("Authorization");

        if (authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            if (jwt.isBlank()) {
                httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Невалидный JWT токен в Bearer Header.");
            } else {
                try {
                    UUID id = jwtUtil.validateTokenAndRetrieveClaim(jwt);
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(id.toString());

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    userDetails.getPassword(),
                                    userDetails.getAuthorities()
                            );
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                } catch (JWTVerificationException exception) {
                    httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "Невалидный JWT токен.");
                }
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}
