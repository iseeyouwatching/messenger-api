package ru.hits.messengerapi.filestorage.controller;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hits.messengerapi.filestorage.service.IntegrationFileService;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер для интеграционных запросов, связанных с файлами.
 */
@RestController
@RequestMapping("/integration/files")
@RequiredArgsConstructor
@Slf4j
@Hidden
public class IntegrationFileController {

    /**
     * Сервис с логикой интеграционных запросов, связанных с файлами.
     */
    private final IntegrationFileService integrationFileService;

    /**
     * Проверка существования файла (аватарки) по идентификатору.
     *
     * @param id идентификатор файла.
     */
    @GetMapping("/check-avatar-id-existence/{id}")
    public void checkAvatarIdExistence(@PathVariable("id") UUID id) {
        log.info("Проверка существования файла (аватарки) с ID: {}", id);
        integrationFileService.checkAvatarIdExistence(id);
    }

    /**
     * Проверка существования списка идентификаторов вложений.
     *
     * @param attachmentsIds список идентификаторов вложений.
     */
    @PostMapping("/check-multi-attachment-existence")
    public void checkMultiAttachmentExistence(@RequestBody List<UUID> attachmentsIds) {
        log.info("Проверка существования списка идентификаторов вложений: {}", attachmentsIds);
        integrationFileService.checkMultiAttachmentsIdsExistence(attachmentsIds);
    }

    /**
     * Получение названия файла по идентификатору.
     *
     * @param id идентификатор файла.
     * @return название файла.
     */
    @GetMapping("/get-filename/{id}")
    public ResponseEntity<String> getFilename(@PathVariable("id") UUID id) {
        log.info("Запрос на получение названия файла с ID: {}", id);
        return new ResponseEntity<>(integrationFileService.getFilename(id), HttpStatus.OK);
    }

    /**
     * Получение размера файла по идентификатору.
     *
     * @param id идентификатор файла.
     * @return размер файла.
     */
    @GetMapping("/get-file-size/{id}")
    public ResponseEntity<String> getFileSize(@PathVariable("id") UUID id) {
        log.info("Запрос на получение размера файла с ID: {}", id);
        return new ResponseEntity<>(integrationFileService.getFileSize(id), HttpStatus.OK);
    }

}
