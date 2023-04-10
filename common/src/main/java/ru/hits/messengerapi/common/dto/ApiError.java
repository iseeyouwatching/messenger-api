package ru.hits.messengerapi.common.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс, представляющий ошибку в ответе API.
 */
@Data
public class ApiError {

    /**
     * Список сообщений об ошибке.
     */
    private List<String> messages;

    /**
     * Конструктор класса.
     *
     * @param message сообщение об ошибке.
     */
    public ApiError(String message) {
        this.messages = new ArrayList<>();
        this.messages.add(message);
    }

}
