package ru.hits.messengerapi.common.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Утилитарный класс для генерации и проверки JWT-токенов.
 */
@Component
@Slf4j
public class JWTUtil {

    /**
     * Секретный ключ.
     */
    @Value("${app.security.jwt-token.secret}")
    private String secret;

    /**
     * Приложение, из которого отправляется токен.
     */
    @Value("${app.security.jwt-token.issuer}")
    private String issuer;

    @Value("${app.security.jwt-token.subject}")
    private String subject;

    /**
     * Метод для генерации JWT-токена.
     *
     * @param id ID пользователя, для которого нужно сгенерировать токен.
     * @return сгенерированный JWT-токен.
     */
    public String generateToken(UUID id, String login, String fullName) {
        Date expirationDate = Date.from(ZonedDateTime.now().plusMinutes(60).toInstant());

        String token = JWT.create()
                .withSubject(subject)
                .withClaim("id", id.toString())
                .withClaim("login", login)
                .withClaim("fullName", fullName)
                .withIssuedAt(new Date())
                .withIssuer(issuer)
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(secret));

        log.info("Сгенерирован JWT-токен для пользователя с ID {}, логином {} и полным именем {}", id, login, fullName);

        return token;
    }

    /**
     * Метод для проверки JWT-токена, который возвращает ID пользователя, указанный в токене.
     *
     * @param token JWT-токен.
     * @return список утверждений JWT-токена.
     * @throws JWTVerificationException если токен не прошел верификацию.
     */
    public List<String> validateTokenAndRetrieveClaim(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject("User details")
                .withIssuer(issuer)
                .build();

        DecodedJWT decodedJWT = verifier.verify(token);

        String id = decodedJWT.getClaim("id").asString();
        String login = decodedJWT.getClaim("login").asString();
        String fullName = decodedJWT.getClaim("fullName").asString();

        List<String> claims = new ArrayList<>();
        claims.add(id);
        claims.add(login);
        claims.add(fullName);

        log.info("JWT-токен для пользователя с ID {} и логином {} прошел верификацию", id, login);

        return claims;
    }

}
