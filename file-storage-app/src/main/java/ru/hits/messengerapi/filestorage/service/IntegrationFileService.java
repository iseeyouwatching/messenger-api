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

@Service
@RequiredArgsConstructor
@Slf4j
public class IntegrationFileService {

    private final FileMetadataRepository fileMetadataRepository;

    public void checkAvatarIdExistence(UUID avatarId) {
        if (fileMetadataRepository.findByObjectName(avatarId).isEmpty()) {
            throw new NotFoundException("Аватарки с ID " + avatarId + " не существует.");
        }
    }

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

    public String getFilename(UUID id) {
        Optional<FileMetadataEntity> fileMetadata = fileMetadataRepository.findByObjectName(id);
        if (fileMetadata.isEmpty()) {
            throw new NotFoundException("Файла с ID " + id + " не существует.");
        }
        else {
            return fileMetadata.get().getFilename();
        }
    }

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
