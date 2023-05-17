package ru.hits.messengerapi.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hits.messengerapi.chat.dto.CreateChatDto;
import ru.hits.messengerapi.chat.dto.UpdateChatDto;
import ru.hits.messengerapi.chat.service.ChatService;

import javax.validation.Valid;

/**
 * Контроллер, отвечающий за обработку запросов, связанных с чатами.
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Чаты.")
public class ChatController {


    /**
     * Сервис чатов.
     */
    private final ChatService chatService;

    /**
     * Создает новый чат с помощью полученных данных из тела запроса.
     *
     * @param createChatDto объект, содержащий данные для создания нового чата.
     */
    @Operation(
            summary = "Создать чат.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping
    public ResponseEntity<Void> createChat(@RequestBody @Valid CreateChatDto createChatDto) {
        chatService.createChat(createChatDto);
        log.info("Чат успешно создан.");
        return ResponseEntity.ok().build();
    }

    /**
     * Обновляет существующий чат на основе полученных данных из тела запроса.
     *
     * @param updateChatDto объект, содержащий данные для обновления чата
     */
    @Operation(
            summary = "Изменить чат.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping
    public ResponseEntity<Void> updateChat(@RequestBody @Valid UpdateChatDto updateChatDto) {
        chatService.updateChat(updateChatDto);
        log.info("Чат успешно обновлен.");
        return ResponseEntity.ok().build();
    }

}
