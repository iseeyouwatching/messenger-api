package ru.hits.messengerapi.chat;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.hits.messengerapi.chat.dto.UpdateChatDto;
import ru.hits.messengerapi.chat.entity.ChatEntity;
import ru.hits.messengerapi.chat.mapper.ChatMapper;
import ru.hits.messengerapi.chat.mapper.ChatUserMapper;
import ru.hits.messengerapi.chat.repository.ChatRepository;
import ru.hits.messengerapi.chat.repository.ChatUserRepository;
import ru.hits.messengerapi.chat.service.ChatService;
import ru.hits.messengerapi.chat.service.IntegrationRequestsService;
import ru.hits.messengerapi.common.exception.ForbiddenException;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.common.security.JwtUserData;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private ChatUserRepository chatUserRepository;

    @Mock
    private IntegrationRequestsService integrationRequestsService;

    @Mock
    private ChatMapper chatMapper;

    @Mock
    private ChatUserMapper chatUserMapper;

    private ChatService chatService;

    private Authentication authentication;

    private SecurityContext securityContext;

    public ChatServiceTest() {
        MockitoAnnotations.openMocks(this);
        authentication = mock(Authentication.class);
        securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        chatService = new ChatService(
                chatRepository,
                chatUserRepository,
                chatMapper,
                chatUserMapper,
                integrationRequestsService
        );
    }

    @Test
    void updateChat_shouldThrowNotFoundExceptionWhenChatNotFound() {
        UpdateChatDto updateChatDto = new UpdateChatDto();
        updateChatDto.setId(UUID.randomUUID());
        updateChatDto.setName("Updated Chat");
        updateChatDto.setUsers(List.of(UUID.randomUUID(), UUID.randomUUID()));

        UUID authenticatedUserId = UUID.randomUUID();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(new JwtUserData("fasgassga", authenticatedUserId, "fsagagas"));
        when(chatRepository.findById(updateChatDto.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> chatService.updateChat(updateChatDto));

        verify(chatRepository).findById(updateChatDto.getId());
    }

    @Test
    void updateChat_shouldThrowForbiddenExceptionWhenUserNotMemberOfChat() {
        UpdateChatDto updateChatDto = new UpdateChatDto();
        updateChatDto.setId(UUID.randomUUID());
        updateChatDto.setName("Updated Chat");
        updateChatDto.setUsers(List.of(UUID.randomUUID(), UUID.randomUUID()));

        ChatEntity chatEntity = new ChatEntity();
        chatEntity.setId(updateChatDto.getId());

        UUID authenticatedUserId = UUID.randomUUID();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(new JwtUserData("fasgassga", authenticatedUserId, "fsagagas"));
        when(chatRepository.findById(updateChatDto.getId())).thenReturn(Optional.of(chatEntity));
        when(chatUserRepository.findByChatIdAndUserId(updateChatDto.getId(), authenticatedUserId)).thenReturn(Optional.empty());

        assertThrows(ForbiddenException.class, () -> chatService.updateChat(updateChatDto));

        verify(chatRepository).findById(updateChatDto.getId());
        verify(chatUserRepository).findByChatIdAndUserId(updateChatDto.getId(), authenticatedUserId);
    }

    @Test
    void updateChat_shouldThrowForbiddenExceptionWhenUserAddsSelfToChat() {
        UpdateChatDto updateChatDto = new UpdateChatDto();
        updateChatDto.setId(UUID.randomUUID());
        updateChatDto.setName("Updated Chat");
        updateChatDto.setUsers(List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));

        ChatEntity chatEntity = new ChatEntity();
        chatEntity.setId(updateChatDto.getId());

        UUID authenticatedUserId = UUID.randomUUID();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(new JwtUserData("fasgassga", authenticatedUserId, "fsagagas"));
        when(chatRepository.findById(updateChatDto.getId())).thenReturn(Optional.of(chatEntity));

        assertThrows(ForbiddenException.class, () -> chatService.updateChat(updateChatDto));

        verify(chatRepository).findById(updateChatDto.getId());
    }

}