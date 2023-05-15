package ru.hits.messengerapi.filestorage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.filestorage.repository.FileMetadataRepository;

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

}
