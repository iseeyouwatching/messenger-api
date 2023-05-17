package ru.hits.messengerapi.chat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hits.messengerapi.chat.controller.CorrespondenceController;
import ru.hits.messengerapi.chat.dto.CorrespondenceInfoDto;
import ru.hits.messengerapi.chat.dto.CorrespondencesPageListDto;
import ru.hits.messengerapi.chat.dto.MessageInCorrespondenceDto;
import ru.hits.messengerapi.chat.dto.PaginationWithChatNameDto;
import ru.hits.messengerapi.chat.service.CorrespondenceService;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CorrespondenceControllerTest {

    private final CorrespondenceService correspondenceService = mock(CorrespondenceService.class);
    private final CorrespondenceController correspondenceController = new CorrespondenceController(correspondenceService);

    @Test
    void getCorrespondenceInfo_shouldReturnCorrespondenceInfo() {
        UUID correspondenceId = UUID.randomUUID();
        CorrespondenceInfoDto correspondenceInfoDto = new CorrespondenceInfoDto();

        when(correspondenceService.getCorrespondenceInfo(correspondenceId)).thenReturn(correspondenceInfoDto);

        ResponseEntity<CorrespondenceInfoDto> response = correspondenceController.getCorrespondenceInfo(correspondenceId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(correspondenceInfoDto, response.getBody());

        verify(correspondenceService, times(1)).getCorrespondenceInfo(correspondenceId);
        verifyNoMoreInteractions(correspondenceService);
    }

    @Test
    void viewCorrespondence_shouldReturnMessageInCorrespondenceList() {
        UUID correspondenceId = UUID.randomUUID();
        List<MessageInCorrespondenceDto> messageList = Collections.singletonList(new MessageInCorrespondenceDto());

        when(correspondenceService.viewCorrespondence(correspondenceId)).thenReturn(messageList);

        ResponseEntity<List<MessageInCorrespondenceDto>> response = correspondenceController.viewCorrespondence(correspondenceId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(messageList, response.getBody());

        verify(correspondenceService, times(1)).viewCorrespondence(correspondenceId);
        verifyNoMoreInteractions(correspondenceService);
    }

    @Test
    void getCorrespondences_shouldReturnCorrespondencesPageList() {
        PaginationWithChatNameDto paginationWithChatNameDto = new PaginationWithChatNameDto();
        CorrespondencesPageListDto correspondencesPageListDto = new CorrespondencesPageListDto();

        when(correspondenceService.getCorrespondences(paginationWithChatNameDto)).thenReturn(correspondencesPageListDto);

        ResponseEntity<CorrespondencesPageListDto> response = correspondenceController.getCorrespondences(paginationWithChatNameDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(correspondencesPageListDto, response.getBody());

        verify(correspondenceService, times(1)).getCorrespondences(paginationWithChatNameDto);
        verifyNoMoreInteractions(correspondenceService);
    }

}
