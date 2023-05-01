package ru.hits.messengerapi.common.exception;

import java.util.ArrayList;
import java.util.List;

public class MultiForbiddenException extends RuntimeException {

    private final List<String> messages;

    public MultiForbiddenException(List<String> messages) {
        this.messages = new ArrayList<>();
        this.messages.addAll(messages);
    }

    public List<String> getMessages() {
        return this.messages;
    }
}
