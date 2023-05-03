package ru.hits.messengerapi.chat.controller;

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
public class CorrespondenceController {

    private final CorrespondenceService correspondenceService;

    @GetMapping("/{id}/get-info")
    public ResponseEntity<CorrespondenceInfoDto> getCorrespondenceInfo(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(correspondenceService.getCorrespondenceInfo(id), HttpStatus.OK);
    }

    @GetMapping("/{id}/view")
    public ResponseEntity<List<MessageInCorrespondenceDto>> viewCorrespondence(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(correspondenceService.viewCorrespondence(id), HttpStatus.OK);
    }

    @PostMapping("/get")
    public ResponseEntity<List<PaginationCorrespondancesDto>> getCorrespondances(@RequestBody @Valid
                                                                                 PaginationWithChatNameDto paginationWithChatNameDto) {
        return new ResponseEntity<>(
                correspondenceService.getCorrespondances(paginationWithChatNameDto),HttpStatus.OK);
    }

}
