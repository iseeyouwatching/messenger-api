package ru.hits.messengerapi.common.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import ru.hits.messengerapi.common.exception.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Обработчик ошибок для {@link RestTemplate}. Реализует интерфейс {@link ResponseErrorHandler}.
 */
@Component
public class RestTemplateErrorHandler implements ResponseErrorHandler {

    /**
     * ObjectMapper для десериализации ответов от сервера.
     */
    private final ObjectMapper objectMapper;

    /**
     * Конструктор класса.
     *
     * @param objectMapper ObjectMapper для десериализации ответов от сервера.
     */
    public RestTemplateErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Проверяет, содержит ли ответ ошибку
     *
     * @param response ответ от сервера.
     * @return true, если ответ содержит ошибку 4xx или 5xx, иначе - false.
     * @throws IOException если произошла ошибка при чтении ответа.
     */
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    /**
     * Обрабатывает ошибку, полученную от сервера.
     *
     * @param response ответ от сервера.
     * @throws IOException если произошла ошибка при чтении ответа.
     * @throws UnauthorizedException если запрос не авторизован.
     * @throws NotFoundException если запрашиваемый ресурс не найден.
     * @throws ConflictException если произошло конфликтное состояние на сервере.
     * @throws ForbiddenException если у пользователя нет прав на выполнение запроса.
     * @throws MultiForbiddenException если у пользователя нет прав на выполнение нескольких запросов.
     * @throws ServiceUnavailableException если сервис сейчас недоступен.
     */
    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().is5xxServerError()) {
            if (response.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
                throw new ServiceUnavailableException("Сервис сейчас недоступен.");
            }
        } else if (response.getStatusCode().is4xxClientError()) {
            if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new UnauthorizedException("Несанкционированный доступ.");
            } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                InputStream responseBody = response.getBody();
                ErrorResponse errorResponse = objectMapper.readValue(responseBody, ErrorResponse.class);
                String errorMessage = errorResponse.getMessages().get(0);
                throw new NotFoundException(errorMessage);
            } else if (response.getStatusCode() == HttpStatus.CONFLICT) {
                InputStream responseBody = response.getBody();
                ErrorResponse errorResponse = objectMapper.readValue(responseBody, ErrorResponse.class);
                if (errorResponse.getMessages().size() > 1) {
                    throw new MultiForbiddenException(errorResponse.getMessages());
                }
                else if (errorResponse.getMessages().size() == 1) {
                    String errorMessage = errorResponse.getMessages().get(0);
                    throw new ConflictException(errorMessage);
                }
            } else if (response.getStatusCode() == HttpStatus.FORBIDDEN) {
                InputStream responseBody = response.getBody();
                ErrorResponse errorResponse = objectMapper.readValue(responseBody, ErrorResponse.class);
                String errorMessage = errorResponse.getMessages().get(0);
                throw new ForbiddenException(errorMessage);
            }
        }
    }

    /**
     * Класс для десериализации ответов от сервера.
     */
    private static class ErrorResponse {

        /**
         * Список сообщений.
         */
        private List<String> messages;

        /**
         * Метод для получения сообщений.
         *
         * @return список сообщений.
         */
        public List<String> getMessages() {
            return messages;
        }

        /**
         * Устанавливает список сообщений.
         *
         * @param messages список сообщений.
         */
        public void setMessages(List<String> messages) {
            this.messages = messages;
        }
    }

}
