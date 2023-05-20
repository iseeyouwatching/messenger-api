package ru.hits.messengerapi.filestorage;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.filestorage.config.MinioConfiguration;
import ru.hits.messengerapi.filestorage.dto.FileDownloadDto;
import ru.hits.messengerapi.filestorage.entity.FileMetadataEntity;
import ru.hits.messengerapi.filestorage.repository.FileMetadataRepository;
import ru.hits.messengerapi.filestorage.service.FileService;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FileServiceTest {

    @Mock
    private MinioClient minioClient;

    @Mock
    private MinioConfiguration minioConfig;

    @Mock
    private FileMetadataRepository fileMetadataRepository;

    @InjectMocks
    private FileService fileService;

    @Test
    public void testUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "image/jpeg",
                "Hello, World!".getBytes()
        );

        String bucketName = "test-bucket";
        when(minioConfig.getBucket()).thenReturn(bucketName);

        String fileId = fileService.upload(file);

        assertNotNull(fileId);
        verify(minioClient, times(1)).putObject(any());
        verify(fileMetadataRepository, times(1)).save(any(FileMetadataEntity.class));
    }

    @Test
    public void testDownload() throws Exception {
        UUID fileId = UUID.randomUUID();
        String bucket = "test-bucket";
        String filename = "test-file.txt";
        String contentType = "text/plain";
        byte[] fileContent = "Test file content".getBytes();

        FileMetadataEntity fileMetadata = new FileMetadataEntity();
        fileMetadata.setBucket(bucket);
        fileMetadata.setObjectName(fileId);
        fileMetadata.setFilename(filename);
        fileMetadata.setContentType(contentType);

        when(fileMetadataRepository.findByObjectName(fileId)).thenReturn(Optional.of(fileMetadata));

        GetObjectResponse getObjectResponse = mock(GetObjectResponse.class);
        when(getObjectResponse.readAllBytes()).thenReturn(fileContent);
        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(getObjectResponse);

        FileDownloadDto result = fileService.download(fileId);

        assertNotNull(result);
        assertArrayEquals(fileContent, result.getIn());
        assertEquals(filename, result.getFilename());

        verify(fileMetadataRepository).findByObjectName(fileId);
        verify(minioClient).getObject(argThat(args ->
                args.bucket().equals(bucket) && args.object().equals(fileId.toString())));
    }

    @Test
    public void testDownload_ThrowsNotFoundException() {
        UUID fileId = UUID.randomUUID();
        when(fileMetadataRepository.findByObjectName(fileId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> fileService.download(fileId));
    }

}
