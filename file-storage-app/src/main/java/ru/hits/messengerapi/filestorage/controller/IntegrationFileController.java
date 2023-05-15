package ru.hits.messengerapi.filestorage.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.messengerapi.filestorage.service.IntegrationFileService;

import java.util.UUID;

@RestController
@RequestMapping("/integration/files")
@RequiredArgsConstructor
@Slf4j
public class IntegrationFileController {

    private final IntegrationFileService integrationFileService;

    @GetMapping("/check-avatar-id-existence/{id}")
    public void checkAvatarIdExistence(@PathVariable("id") UUID id) {
        integrationFileService.checkAvatarIdExistence(id);
    }

}
