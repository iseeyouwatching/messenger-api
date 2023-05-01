package ru.hits.messengerapi.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hits.messengerapi.chat.dto.CreateChatDto;
import ru.hits.messengerapi.chat.dto.UpdateChatDto;
import ru.hits.messengerapi.chat.service.ChatService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public void createChat(@RequestBody @Valid CreateChatDto createChatDto) {
        chatService.createChat(createChatDto);
    }

    @PutMapping
    public void updateChat(@RequestBody @Valid UpdateChatDto updateChatDto) {
        chatService.updateChat(updateChatDto);
    }

}
