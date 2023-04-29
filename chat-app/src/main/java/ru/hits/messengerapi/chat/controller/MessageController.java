package ru.hits.messengerapi.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.messengerapi.chat.dto.DialogueMessageDto;
import ru.hits.messengerapi.chat.service.MessageService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final MessageService messageService;

    @PostMapping("send-message-to-dialogue")
    private void sendMessageToDialogue(@RequestBody @Valid DialogueMessageDto dialogueMessageDto) {
        messageService.sendMessageToDialogue(dialogueMessageDto);
    }

}
