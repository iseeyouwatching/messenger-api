package ru.hits.messengerapi.filestorage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hits.messengerapi.filestorage.service.IntegrationFileService;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationFileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IntegrationFileService integrationFileService;

    @Test
    public void testCheckAvatarIdExistence() throws Exception {
        UUID fileId = UUID.randomUUID();

        mockMvc.perform(get("/integration/files/check-avatar-id-existence/{id}", fileId))
                .andExpect(status().isOk());
    }

    @Test
    public void testCheckMultiAttachmentExistence() throws Exception {
        List<UUID> attachmentIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(attachmentIds);

        mockMvc.perform(MockMvcRequestBuilders.post("/integration/files/check-multi-attachment-existence")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetFilename() throws Exception {
        UUID fileId = UUID.randomUUID();
        String filename = "test.txt";

        given(integrationFileService.getFilename(fileId)).willReturn(filename);

        mockMvc.perform(get("/integration/files/get-filename/{id}", fileId))
                .andExpect(status().isOk())
                .andExpect(content().string(filename));
    }

    @Test
    public void testGetFileSize() throws Exception {
        UUID fileId = UUID.randomUUID();
        String fileSize = "1024MB";

        given(integrationFileService.getFileSize(fileId)).willReturn(fileSize);

        mockMvc.perform(get("/integration/files/get-file-size/{id}", fileId))
                .andExpect(status().isOk())
                .andExpect(content().string(fileSize));
    }

}
