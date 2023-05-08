package ru.hits.messengerapi.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.hits.messengerapi.chat.dto.CreateChatDto;
import ru.hits.messengerapi.chat.dto.UpdateChatDto;
import ru.hits.messengerapi.chat.service.ChatService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Чаты.")
public class ChatController {

    private final ChatService chatService;

    @Operation(
            summary = "Создать чат.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping
    public void createChat(@RequestBody @Valid CreateChatDto createChatDto) {
        chatService.createChat(createChatDto);
    }

    @Operation(
            summary = "Изменить чат.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping
    public void updateChat(@RequestBody @Valid UpdateChatDto updateChatDto) {
        chatService.updateChat(updateChatDto);
    }

}
