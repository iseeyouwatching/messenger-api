package ru.hits.messengerapi.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

import static ru.hits.messengerapi.common.security.SecurityConst.HEADER_API_KEY;
import static ru.hits.messengerapi.common.security.SecurityConst.HEADER_JWT;

/**
 * Фильтр проверки ключа API KEY в интеграционных запросах
 */
@RequiredArgsConstructor
class ApiIntegrationFilter extends OncePerRequestFilter {

    private final String apiKey;

    @Override
    protected void doFilterInternal(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = httpServletRequest.getHeader(HEADER_JWT);
        if (authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ") &&
                Objects.equals(httpServletRequest.getHeader(HEADER_API_KEY), apiKey)) {
            SecurityContextHolder.getContext().setAuthentication(new IntegrationAuthentication());
        }
        else {
            System.out.println("ПРАЫФАЫААЫАФЫИВЕТ!!");
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
