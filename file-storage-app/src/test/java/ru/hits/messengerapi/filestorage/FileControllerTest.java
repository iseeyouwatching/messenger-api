package ru.hits.messengerapi.filestorage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.hits.messengerapi.filestorage.dto.FileDownloadDto;
import ru.hits.messengerapi.filestorage.service.FileService;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @Test
    @WithMockUser
    public void testUploadFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "Test file content".getBytes());
        mockMvc.perform(multipart("/api/files/upload")
                        .file(file)
                        .with(user("username").password("password")))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testDownloadFile() throws Exception {
        UUID fileId = UUID.randomUUID();
        FileDownloadDto fileDownloadDto = new FileDownloadDto(
                "Test file content".getBytes(),
                "test.txt"
        );

        given(fileService.download(any(UUID.class))).willReturn(fileDownloadDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/files/download/{id}", fileId)
                        .accept(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", "filename=test.txt"))
                .andExpect(MockMvcResultMatchers.content().bytes(fileDownloadDto.getIn()));
    }

}
