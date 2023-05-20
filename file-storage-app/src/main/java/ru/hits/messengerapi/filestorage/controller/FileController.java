package ru.hits.messengerapi.filestorage.controller;

import io.minio.errors.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hits.messengerapi.filestorage.dto.FileDownloadDto;
import ru.hits.messengerapi.filestorage.service.FileService;

import javax.validation.Valid;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Контроллер файлов.
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Работа с файлами.")
public class FileController {

    /**
     * Контроллер, обрабатывающий запросы, связанные с файлами.
     */
    private final FileService fileService;

    /**
     * Метод для загрузки файла на сервер.
     *
     * @param file загружаемый файл.
     * @return идентификатор загруженного файла.
     * @throws IOException  если произошла ошибка ввода-вывода при загрузке файла.
     * @throws ServerException если сервер вернул ошибку при загрузке файла.
     * @throws InsufficientDataException если недостаточно данных для загрузки файла.
     * @throws ErrorResponseException если сервер вернул ошибку при загрузке файла.
     * @throws NoSuchAlgorithmException если алгоритм хэширования не поддерживается.
     * @throws InvalidKeyException если указан недопустимый ключ.
     * @throws InvalidResponseException если ответ сервера недопустим.
     * @throws XmlParserException если произошла ошибка при разборе XML.
     * @throws InternalException если произошла внутренняя ошибка сервера.
     */
    @Operation(summary = "Загрузка файла.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String upload(@RequestParam("file") MultipartFile file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return fileService.upload(file);
    }

    /**
     * Метод для получения загруженного файла.
     *
     * @param id идентификатор файла.
     * @return файл.
     */
    @Operation(summary = "Получение файла по id.")
    @GetMapping(value = "/download/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> download(@PathVariable("id") UUID id) {
        FileDownloadDto fileDownloadDto = fileService.download(id);
        return ResponseEntity.ok()
                .header("Content-Disposition", "filename=" + fileDownloadDto.getFilename())
                .body(fileDownloadDto.getIn());
    }

}
