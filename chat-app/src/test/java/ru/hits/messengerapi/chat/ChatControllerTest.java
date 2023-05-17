package ru.hits.messengerapi.chat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hits.messengerapi.chat.controller.ChatController;
import ru.hits.messengerapi.chat.dto.CreateChatDto;
import ru.hits.messengerapi.chat.dto.UpdateChatDto;
import ru.hits.messengerapi.chat.service.ChatService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatControllerTest {

    private ChatController chatController;

    @Mock
    private ChatService chatService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        chatController = new ChatController(chatService);
    }

    @Test
    void createChat_shouldCallChatServiceCreateChat() {
        CreateChatDto createChatDto = new CreateChatDto();

        chatController.createChat(createChatDto);

        verify(chatService, times(1)).createChat(createChatDto);
    }

    @Test
    void createChat_shouldReturnHttpStatusOk() {
        CreateChatDto createChatDto = new CreateChatDto();

        ResponseEntity<Void> response = chatController.createChat(createChatDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void updateChat_shouldCallChatServiceUpdateChat() {
        UpdateChatDto updateChatDto = new UpdateChatDto();

        chatController.updateChat(updateChatDto);

        verify(chatService, times(1)).updateChat(updateChatDto);
    }

    @Test
    void updateChat_shouldReturnHttpStatusOk() {
        UpdateChatDto updateChatDto = new UpdateChatDto();

        ResponseEntity<Void> response = chatController.updateChat(updateChatDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

}

