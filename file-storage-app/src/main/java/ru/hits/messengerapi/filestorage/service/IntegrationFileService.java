package ru.hits.messengerapi.filestorage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.filestorage.entity.FileMetadataEntity;
import ru.hits.messengerapi.filestorage.repository.FileMetadataRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис с логикой интеграционных запросов, которые посылаются в сервис файлового хранилища.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IntegrationFileService {

    /**
     * Репозиторий для работы с метаданными файлов.
     */
    private final FileMetadataRepository fileMetadataRepository;

    /**
     * Проверяет существование аватарки по указанному идентификатору.
     *
     * @param avatarId идентификатор аватарки.
     * @throws NotFoundException если аватарка с указанным идентификатором не существует.
     */
    public void checkAvatarIdExistence(UUID avatarId) {
        if (fileMetadataRepository.findByObjectName(avatarId).isEmpty()) {
            throw new NotFoundException("Аватарки с ID " + avatarId + " не существует.");
        }
    }

    /**
     * Проверяет существование нескольких вложений по указанным идентификаторам.
     *
     * @param attachmentsIds список идентификаторов вложений.
     * @throws NotFoundException если хотя бы одно вложение с указанным идентификатором не существует.
     */
    public void checkMultiAttachmentsIdsExistence(List<UUID> attachmentsIds) {
        List<UUID> badIds = new ArrayList<>();
        for (int i = 0; i < attachmentsIds.size(); i++) {
            if (fileMetadataRepository.findByObjectName(attachmentsIds.get(i)).isEmpty()) {
                badIds.add(attachmentsIds.get(i));
            }
        }
        if (!badIds.isEmpty()) {
            throw new NotFoundException("Вложений с ID " + badIds + " не существует.");
        }
    }

    /**
     * Получает имя файла по указанному идентификатору.
     *
     * @param id идентификатор файла.
     * @return имя файла.
     * @throws NotFoundException если файл с указанным идентификатором не существует.
     */
    public String getFilename(UUID id) {
        Optional<FileMetadataEntity> fileMetadata = fileMetadataRepository.findByObjectName(id);
        if (fileMetadata.isEmpty()) {
            throw new NotFoundException("Файла с ID " + id + " не существует.");
        }
        else {
            return fileMetadata.get().getFilename();
        }
    }

    /**
     * Получает размер файла по указанному идентификатору.
     *
     * @param id идентификатор файла.
     * @return размер файла.
     * @throws NotFoundException если файл с указанным идентификатором не существует.
     */
    public String getFileSize(UUID id) {
        Optional<FileMetadataEntity> fileMetadata = fileMetadataRepository.findByObjectName(id);
        if (fileMetadata.isEmpty()) {
            throw new NotFoundException("Файла с ID " + id + " не существует.");
        }
        else {
            return fileMetadata.get().getSize();
        }
    }

}
