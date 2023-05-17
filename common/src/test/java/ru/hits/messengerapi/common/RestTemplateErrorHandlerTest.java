package ru.hits.messengerapi.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import ru.hits.messengerapi.common.controller.RestTemplateErrorHandler;
import ru.hits.messengerapi.common.exception.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class RestTemplateErrorHandlerTest {

    private RestTemplateErrorHandler restTemplateErrorHandler;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ClientHttpResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        restTemplateErrorHandler = new RestTemplateErrorHandler(objectMapper);
    }

    @Test
    void hasError_shouldReturnTrue_whenResponseIs4xxClientError() throws IOException {
        when(response.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);

        boolean result = restTemplateErrorHandler.hasError(response);

        assertTrue(result);
    }

    @Test
    void hasError_shouldReturnTrue_whenResponseIs5xxServerError() throws IOException {
        when(response.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);

        boolean result = restTemplateErrorHandler.hasError(response);

        assertTrue(result);
    }

    @Test
    void hasError_shouldReturnFalse_whenResponseIs2xxSuccess() throws IOException {
        when(response.getStatusCode()).thenReturn(HttpStatus.OK);

        boolean result = restTemplateErrorHandler.hasError(response);

        assertFalse(result);
    }

    @Test
    void handleError_shouldThrowServiceUnavailableException_whenResponseStatusIs503() throws IOException {
        when(response.getStatusCode()).thenReturn(HttpStatus.SERVICE_UNAVAILABLE);

        assertThrows(ServiceUnavailableException.class, () -> restTemplateErrorHandler.handleError(response));
    }

    @Test
    void handleError_shouldThrowUnauthorizedException_whenResponseStatusIs401() throws IOException {
        when(response.getStatusCode()).thenReturn(HttpStatus.UNAUTHORIZED);

        assertThrows(UnauthorizedException.class, () -> restTemplateErrorHandler.handleError(response));
    }

    @Test
    void handleError_shouldThrowNotFoundException_whenResponseStatusIs404() throws IOException {
        when(response.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND);

        String errorMessage = "Ресурс не найден";
        InputStream responseBody = new ByteArrayInputStream(errorMessage.getBytes(StandardCharsets.UTF_8));
        when(response.getBody()).thenReturn(responseBody);

        RestTemplateErrorHandler.ErrorResponse errorResponse = createErrorResponse(Arrays.asList(errorMessage));
        when(objectMapper.readValue(responseBody, RestTemplateErrorHandler.ErrorResponse.class)).thenReturn(errorResponse);

        assertThrows(NotFoundException.class, () -> restTemplateErrorHandler.handleError(response));
    }

    @Test
    void handleError_shouldThrowConflictException_whenResponseStatusIs409() throws IOException {
        when(response.getStatusCode()).thenReturn(HttpStatus.CONFLICT);

        String errorMessage = "Произошел конфликт";
        InputStream responseBody = new ByteArrayInputStream(errorMessage.getBytes(StandardCharsets.UTF_8));
        when(response.getBody()).thenReturn(responseBody);

        RestTemplateErrorHandler.ErrorResponse errorResponse = createErrorResponse(Arrays.asList(errorMessage));
        when(objectMapper.readValue(responseBody, RestTemplateErrorHandler.ErrorResponse.class)).thenReturn(errorResponse);

        assertThrows(ConflictException.class, () -> restTemplateErrorHandler.handleError(response));
    }

    @Test
    void handleError_shouldThrowForbiddenException_whenResponseStatusIs403_andErrorMessageListSizeIs1() throws IOException {
        when(response.getStatusCode()).thenReturn(HttpStatus.FORBIDDEN);

        String errorMessage = "Доступ запрещен";
        InputStream responseBody = new ByteArrayInputStream(errorMessage.getBytes(StandardCharsets.UTF_8));
        when(response.getBody()).thenReturn(responseBody);

        RestTemplateErrorHandler.ErrorResponse errorResponse = createErrorResponse(Arrays.asList(errorMessage));
        when(objectMapper.readValue(responseBody, RestTemplateErrorHandler.ErrorResponse.class)).thenReturn(errorResponse);

        assertThrows(ForbiddenException.class, () -> restTemplateErrorHandler.handleError(response));
    }

    @Test
    void handleError_shouldThrowMultiForbiddenException_whenResponseStatusIs403_andErrorMessageListSizeIsGreaterThan1() throws IOException {
        when(response.getStatusCode()).thenReturn(HttpStatus.FORBIDDEN);

        List<String> errorMessages = Arrays.asList("Доступ запрещен", "Нет разрешения");
        InputStream responseBody = new ByteArrayInputStream(errorMessages.toString().getBytes(StandardCharsets.UTF_8));
        when(response.getBody()).thenReturn(responseBody);

        RestTemplateErrorHandler.ErrorResponse errorResponse = createErrorResponse(errorMessages);
        when(objectMapper.readValue(responseBody, RestTemplateErrorHandler.ErrorResponse.class)).thenReturn(errorResponse);

        assertThrows(MultiForbiddenException.class, () -> restTemplateErrorHandler.handleError(response));
    }

    private RestTemplateErrorHandler.ErrorResponse createErrorResponse(List<String> errorMessages) {
        RestTemplateErrorHandler.ErrorResponse errorResponse = new RestTemplateErrorHandler.ErrorResponse();
        errorResponse.setMessages(errorMessages);
        return errorResponse;
    }

}
