package ru.hits.messengerapi.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.hits.messengerapi.common.dto.ApiError;
import ru.hits.messengerapi.common.exception.BadRequestException;
import ru.hits.messengerapi.common.exception.ConflictException;
import ru.hits.messengerapi.common.exception.NotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        Map<String, List<String>> errors = new HashMap<>();

        exception
                .getBindingResult()
                .getAllErrors()
                .forEach(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String message = error.getDefaultMessage();

                    if (message != null) {
                        if (errors.containsKey(fieldName)) {
                            errors.get(fieldName).add(message);
                        } else {
                            List<String> newErrorList = new ArrayList<>();
                            newErrorList.add(message);

                            errors.put(fieldName, newErrorList);
                        }
                    }
                });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(NotFoundException exception,
                                                            WebRequest request
    ) {
        return new ResponseEntity<>(new ApiError(exception.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequestException(BadRequestException exception,
                                                              WebRequest request
    ) {
        return new ResponseEntity<>(new ApiError(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflictException(ConflictException exception,
                                                            WebRequest request
    ) {
        return new ResponseEntity<>(new ApiError(exception.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedInternalException(Exception exception,
                                                                      WebRequest request
    ) {
        return new ResponseEntity<>(
                new ApiError("Непредвиденная внутренняя ошибка сервера"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}