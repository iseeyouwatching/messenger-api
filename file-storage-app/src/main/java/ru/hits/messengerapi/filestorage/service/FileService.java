package ru.hits.messengerapi.filestorage.service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.filestorage.config.MinioConfiguration;
import ru.hits.messengerapi.filestorage.dto.FileDownloadDto;
import ru.hits.messengerapi.filestorage.entity.FileMetadataEntity;
import ru.hits.messengerapi.filestorage.repository.FileMetadataRepository;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Сервис файлов.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    /**
     * Клиент MinIO.
     */
    private final MinioClient minioClient;

    /**
     * Конфигурация MinIO.
     */
    private final MinioConfiguration minioConfig;

    /**
     * Репозиторий для работы с метаданными файлов.
     */
    private final FileMetadataRepository fileMetadataRepository;

    /**
     * Инициализация сервиса.
     */
    @PostConstruct
    public void init() {
        log.info("Minio configs: {}", minioConfig);
    }

    /**
     * Загрузка файла.
     *
     * @param file загружаемый файл.
     * @return уникальный идентификатор файла.
     * @throws ServerException            если произошла ошибка на сервере MinIO.
     * @throws InsufficientDataException  если данные файла недостаточны.
     * @throws ErrorResponseException     если получен неправильный ответ от сервера MinIO.
     * @throws IOException                если произошла ошибка ввода-вывода.
     * @throws NoSuchAlgorithmException  если используется неподдерживаемый алгоритм хэширования.
     * @throws InvalidKeyException        если предоставлен неверный ключ.
     * @throws InvalidResponseException   если получен неверный ответ от сервера MinIO.
     * @throws XmlParserException         если произошла ошибка парсинга XML.
     * @throws InternalException          если произошла внутренняя ошибка сервера MinIO.
     */
    @Transactional
    public String upload(MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        var id = UUID.randomUUID().toString();
        minioClient.putObject(
                PutObjectArgs.builder()
                .bucket(minioConfig.getBucket())
                .object(id)
                .stream(new ByteArrayInputStream(file.getBytes()), file.getSize(), -1)
                .contentType(file.getContentType())
                .build()
        );

        FileMetadataEntity fileMetadata = new FileMetadataEntity();
        fileMetadata.setFilename(file.getOriginalFilename());
        fileMetadata.setBucket(minioConfig.getBucket());
        fileMetadata.setObjectName(UUID.fromString(id));
        fileMetadata.setContentType(file.getContentType());
        fileMetadata.setCreationDate(LocalDate.now());
        long fileSize = file.getSize();
        double fileSizeInMB = (double) fileSize / (1024 * 1024);
        fileMetadata.setSize(fileSizeInMB + "MB");
        fileMetadataRepository.save(fileMetadata);

        return id;
    }

    /**
     * Загрузка файла по уникальному идентификатору.
     *
     * @param id уникальный идентификатор файла.
     * @return объект {@link FileDownloadDto}, содержащий данные файла для скачивания.
     * @throws NotFoundException если файл с указанным идентификатором не найден.
     */
    public FileDownloadDto download(UUID id) {
        FileMetadataEntity fileMetadata = fileMetadataRepository.findByObjectName(id)
                .orElseThrow(() -> new NotFoundException("Файл с ID " + id + " не найден."));

        var args = GetObjectArgs.builder()
                .bucket(fileMetadata.getBucket())
                .object(String.valueOf(id))
                .build();
        try (var in = minioClient.getObject(args)) {
            return new FileDownloadDto(in.readAllBytes(), fileMetadata.getFilename());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при загрузке файла с ID " + id + ".", e);
        }
    }

}