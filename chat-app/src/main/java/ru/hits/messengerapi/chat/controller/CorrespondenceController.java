package ru.hits.messengerapi.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.messengerapi.chat.dto.CorrespondenceInfoDto;
import ru.hits.messengerapi.chat.service.CorrespondenceService;

import java.util.UUID;

@RestController
@RequestMapping("/api/chat/correspondence")
@RequiredArgsConstructor
@Slf4j
public class CorrespondenceController {

    private final CorrespondenceService correspondenceService;

    @GetMapping("/{id}")
    public ResponseEntity<CorrespondenceInfoDto> getCorrespondenceInfo(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(correspondenceService.getCorrespondenceInfo(id), HttpStatus.OK);
    }

}
