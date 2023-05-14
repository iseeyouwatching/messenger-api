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

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return fileService.upload(file);
    }

    @GetMapping(value = "/download/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> download(@PathVariable("id") UUID id) {
        FileDownloadDto fileDownloadDto = fileService.download(id);
        return ResponseEntity.ok()
                .header("Content-Disposition", "filename=" + fileDownloadDto.getFilename())
                .body(fileDownloadDto.getIn());
    }

}
