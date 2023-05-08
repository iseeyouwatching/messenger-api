package ru.hits.messengerapi.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hits.messengerapi.chat.dto.CorrespondenceInfoDto;
import ru.hits.messengerapi.chat.dto.MessageInCorrespondenceDto;
import ru.hits.messengerapi.chat.dto.PaginationCorrespondancesDto;
import ru.hits.messengerapi.chat.dto.PaginationWithChatNameDto;
import ru.hits.messengerapi.chat.service.CorrespondenceService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat/correspondence")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Переписки.")
public class CorrespondenceController {

    private final CorrespondenceService correspondenceService;

    @Operation(
            summary = "Получить информацию о переписке по ID.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}/get-info")
    public ResponseEntity<CorrespondenceInfoDto> getCorrespondenceInfo(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(correspondenceService.getCorrespondenceInfo(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Посмотреть переписку.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}/view")
    public ResponseEntity<List<MessageInCorrespondenceDto>> viewCorrespondence(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(correspondenceService.viewCorrespondence(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Посмотреть список переписок.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/get")
    public ResponseEntity<List<PaginationCorrespondancesDto>> getCorrespondences(@RequestBody @Valid
                                                                                 PaginationWithChatNameDto paginationWithChatNameDto) {
        return new ResponseEntity<>(
                correspondenceService.getCorrespondences(paginationWithChatNameDto),HttpStatus.OK);
    }

}
