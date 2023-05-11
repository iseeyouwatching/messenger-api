package ru.hits.messengerapi.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hits.messengerapi.chat.dto.*;
import ru.hits.messengerapi.chat.service.CorrespondenceService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * Контроллер, отвечающий за обработку запросов, связанных с переписками.
 */
@RestController
@RequestMapping("/api/chat/correspondence")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Переписки.")
public class CorrespondenceController {

    /**
     * Сервис переписок.
     */
    private final CorrespondenceService correspondenceService;

    /**
     * Получает информацию о переписке по ее идентификатору.
     *
     * @param id идентификатор переписки.
     * @return информация о переписке в виде объекта {@link CorrespondenceInfoDto}.
     */
    @Operation(
            summary = "Получить информацию о переписке по ID.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}/get-info")
    public ResponseEntity<CorrespondenceInfoDto> getCorrespondenceInfo(@PathVariable("id") UUID id) {
        log.info("Получение информации о переписке с ID: {}", id);
        return new ResponseEntity<>(correspondenceService.getCorrespondenceInfo(id), HttpStatus.OK);
    }

    /**
     * Возвращает список сообщений в заданной переписке.
     *
     * @param id идентификатор переписки.
     * @return список сообщений в виде объекта {@link MessageInCorrespondenceDto}.
     */
    @Operation(
            summary = "Посмотреть переписку.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}/view")
    public ResponseEntity<List<MessageInCorrespondenceDto>> viewCorrespondence(@PathVariable("id") UUID id) {
        log.info("Просмотр переписки с ID: {}", id);
        return new ResponseEntity<>(correspondenceService.viewCorrespondence(id), HttpStatus.OK);
    }

    /**
     * Возвращает список переписок, удовлетворяющих заданным критериям поиска и параметрам пагинации.
     *
     * @param paginationWithChatNameDto объект, содержащий параметры пагинации и имя чата
     *                                  для фильтрации результатов.
     * @return список переписок в виде объекта {@link CorrespondencesPageListDto}.
     */
    @Operation(
            summary = "Посмотреть список переписок.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/get")
    public ResponseEntity<CorrespondencesPageListDto> getCorrespondences(@RequestBody @Valid
                                                                                 PaginationWithChatNameDto paginationWithChatNameDto) {
        log.info("Получение списка переписок: {}", paginationWithChatNameDto);
        return new ResponseEntity<>(
                correspondenceService.getCorrespondences(paginationWithChatNameDto),HttpStatus.OK);
    }

}
