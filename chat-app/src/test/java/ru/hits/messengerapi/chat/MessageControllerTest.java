package ru.hits.messengerapi.chat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hits.messengerapi.chat.controller.MessageController;
import ru.hits.messengerapi.chat.dto.ChatMessageDto;
import ru.hits.messengerapi.chat.dto.DialogueMessageDto;
import ru.hits.messengerapi.chat.dto.MessageDto;
import ru.hits.messengerapi.chat.dto.SearchStringDto;
import ru.hits.messengerapi.chat.service.MessageService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MessageControllerTest {

    private final MessageService messageService = mock(MessageService.class);
    private final MessageController messageController = new MessageController(messageService);

    @Test
    void sendMessageToDialogue_shouldCallServiceAndReturnHttpStatusOk() {
        DialogueMessageDto dialogueMessageDto = new DialogueMessageDto();

        messageController.sendMessageToDialogue(dialogueMessageDto);

        verify(messageService, times(1)).sendMessageToDialogue(dialogueMessageDto);
        verifyNoMoreInteractions(messageService);
    }

    @Test
    void sendMessageToChat_shouldCallServiceAndReturnHttpStatusOk() {
        ChatMessageDto chatMessageDto = new ChatMessageDto();

        messageController.sendMessageToChat(chatMessageDto);

        verify(messageService, times(1)).sendMessageToChat(chatMessageDto);
        verifyNoMoreInteractions(messageService);
    }

    @Test
    void searchMessages_shouldReturnListOfMessages() {
        SearchStringDto searchStringDto = new SearchStringDto();
        List<MessageDto> messageList = Collections.singletonList(new MessageDto());

        when(messageService.searchMessages(searchStringDto)).thenReturn(messageList);

        ResponseEntity<List<MessageDto>> response = messageController.searchMessages(searchStringDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(messageList, response.getBody());

        verify(messageService, times(1)).searchMessages(searchStringDto);
        verifyNoMoreInteractions(messageService);
    }

}
