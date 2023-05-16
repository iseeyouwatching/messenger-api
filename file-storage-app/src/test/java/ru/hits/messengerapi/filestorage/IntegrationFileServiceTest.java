package ru.hits.messengerapi.filestorage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.filestorage.entity.FileMetadataEntity;
import ru.hits.messengerapi.filestorage.repository.FileMetadataRepository;
import ru.hits.messengerapi.filestorage.service.IntegrationFileService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IntegrationFileServiceTest {

    @Mock
    private FileMetadataRepository fileMetadataRepository;

    @InjectMocks
    private IntegrationFileService fileService;

    @Test
    public void testCheckAvatarIdExistence_FileExists() {
        UUID avatarId = UUID.randomUUID();
        FileMetadataEntity fileMetadata = new FileMetadataEntity();

        when(fileMetadataRepository.findByObjectName(avatarId)).thenReturn(Optional.of(fileMetadata));

        fileService.checkAvatarIdExistence(avatarId);
    }

    @Test(expected = NotFoundException.class)
    public void testCheckAvatarIdExistence_FileNotFound() {
        UUID avatarId = UUID.randomUUID();

        when(fileMetadataRepository.findByObjectName(avatarId)).thenReturn(Optional.empty());

        fileService.checkAvatarIdExistence(avatarId);
    }

    @Test
    public void testCheckMultiAttachmentsIdsExistence_AllFilesExist() {
        List<UUID> attachmentIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
        FileMetadataEntity fileMetadata = new FileMetadataEntity();

        when(fileMetadataRepository.findByObjectName(attachmentIds.get(0))).thenReturn(Optional.of(fileMetadata));
        when(fileMetadataRepository.findByObjectName(attachmentIds.get(1))).thenReturn(Optional.of(fileMetadata));

        fileService.checkMultiAttachmentsIdsExistence(attachmentIds);
    }

    @Test(expected = NotFoundException.class)
    public void testCheckMultiAttachmentsIdsExistence_OneFileNotFound() {
        List<UUID> attachmentIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
        FileMetadataEntity fileMetadata = new FileMetadataEntity();

        when(fileMetadataRepository.findByObjectName(attachmentIds.get(0))).thenReturn(Optional.of(fileMetadata));
        when(fileMetadataRepository.findByObjectName(attachmentIds.get(1))).thenReturn(Optional.empty());

        fileService.checkMultiAttachmentsIdsExistence(attachmentIds);
    }

    @Test
    public void testGetFilename_FileExists() {
        UUID fileId = UUID.randomUUID();
        String filename = "test-file.txt";
        FileMetadataEntity fileMetadata = new FileMetadataEntity();
        fileMetadata.setFilename(filename);

        when(fileMetadataRepository.findByObjectName(fileId)).thenReturn(Optional.of(fileMetadata));

        String result = fileService.getFilename(fileId);

        assertEquals(filename, result);
    }

    @Test(expected = NotFoundException.class)
    public void testGetFilename_FileNotFound() {
        UUID fileId = UUID.randomUUID();

        when(fileMetadataRepository.findByObjectName(fileId)).thenReturn(Optional.empty());

        fileService.getFilename(fileId);
    }

    @Test
    public void testGetFileSize_FileExists() {
        UUID fileId = UUID.randomUUID();
        String fileSize = "10MB";
        FileMetadataEntity fileMetadata = new FileMetadataEntity();
        fileMetadata.setSize(fileSize);

        when(fileMetadataRepository.findByObjectName(fileId)).thenReturn(Optional.of(fileMetadata));

        String result = fileService.getFileSize(fileId);

        assertEquals(fileSize, result);
    }

    @Test(expected = NotFoundException.class)
    public void testGetFileSize_FileNotFound() {
        UUID fileId = UUID.randomUUID();

        when(fileMetadataRepository.findByObjectName(fileId)).thenReturn(Optional.empty());

        fileService.getFileSize(fileId);
    }

}
