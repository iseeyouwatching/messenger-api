package ru.hits.messengerapi.common.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import ru.hits.messengerapi.common.exception.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class RestTemplateErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper;

    public RestTemplateErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }
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
            }
        }
    }

    private static class ErrorResponse {
        private List<String> messages;

        public List<String> getMessages() {
            return messages;
        }

        public void setMessages(List<String> messages) {
            this.messages = messages;
        }
    }

}
