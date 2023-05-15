package ru.hits.messengerapi.filestorage.controller;

import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hits.messengerapi.filestorage.dto.FileDownloadDto;
import ru.hits.messengerapi.filestorage.service.FileService;

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
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return fileService.upload(file);
    }

    /**
     * Метод для получения загруженного файла.
     *
     * @param id идентификатор файла.
     * @return файл.
     */
    @GetMapping(value = "/download/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> download(@PathVariable("id") UUID id) {
        FileDownloadDto fileDownloadDto = fileService.download(id);
        return ResponseEntity.ok()
                .header("Content-Disposition", "filename=" + fileDownloadDto.getFilename())
                .body(fileDownloadDto.getIn());
    }

}
