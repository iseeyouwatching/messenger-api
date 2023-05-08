package ru.hits.messengerapi.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.messengerapi.chat.dto.ChatMessageDto;
import ru.hits.messengerapi.chat.dto.DialogueMessageDto;
import ru.hits.messengerapi.chat.dto.MessageDto;
import ru.hits.messengerapi.chat.dto.SearchStringDto;
import ru.hits.messengerapi.chat.service.MessageService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/chat/message")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Сообщения.")
public class MessageController {

    private final MessageService messageService;

    @Operation(
            summary = "Отправить сообщение в личные сообщения.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/dialogue")
    public void sendMessageToDialogue(@RequestBody @Valid DialogueMessageDto dialogueMessageDto) {
        messageService.sendMessageToDialogue(dialogueMessageDto);
    }

    @Operation(
            summary = "Отправить сообщение в чат.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/chat")
    public void sendMessageToChat(@RequestBody @Valid ChatMessageDto chatMessageDto) {
        messageService.sendMessageToChat(chatMessageDto);
    }

    @Operation(
            summary = "Поиск сообщений.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/search")
    public ResponseEntity<List<MessageDto>> searchMessages(@RequestBody @Valid SearchStringDto searchStringDto) {
        return new ResponseEntity<>(messageService.searchMessages(searchStringDto), HttpStatus.OK);
    }

}
