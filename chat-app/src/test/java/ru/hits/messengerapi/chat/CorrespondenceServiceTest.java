package ru.hits.messengerapi.chat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.hits.messengerapi.chat.dto.*;
import ru.hits.messengerapi.chat.entity.*;
import ru.hits.messengerapi.chat.enumeration.ChatType;
import ru.hits.messengerapi.chat.repository.ChatRepository;
import ru.hits.messengerapi.chat.repository.ChatUserRepository;
import ru.hits.messengerapi.chat.repository.MessageRepository;
import ru.hits.messengerapi.chat.service.CorrespondenceService;
import ru.hits.messengerapi.common.exception.ForbiddenException;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.common.helpingservices.CheckPaginationInfoService;
import ru.hits.messengerapi.common.security.JwtUserData;
import ru.hits.messengerapi.chat.service.IntegrationRequestsService;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CorrespondenceServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private ChatUserRepository chatUserRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private IntegrationRequestsService integrationRequestsService;

    @Mock
    private CheckPaginationInfoService checkPaginationInfoService;

    @InjectMocks
    private CorrespondenceService correspondenceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCorrespondenceInfo_InvalidId_ThrowsNotFoundException() {
        UUID chatId = UUID.randomUUID();
        when(chatRepository.findById(chatId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> correspondenceService.getCorrespondenceInfo(chatId));
    }

    @Test
    void getCorrespondenceInfo_UserNotInChat_ThrowsForbiddenException() {
        UUID chatId = UUID.randomUUID();
        UUID authenticatedUserId = UUID.randomUUID();
        ChatEntity chat = new ChatEntity();
        chat.setId(chatId);
        chat.setChatType(ChatType.CHAT);
        chat.setName("Test Chat");
        chat.setAvatarId(UUID.randomUUID());
        chat.setAdminId(UUID.randomUUID());
        chat.setCreationDate(LocalDate.now());

        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = mock(Authentication.class);

        JwtUserData jwtUserData = new JwtUserData("gsagvxcvz", authenticatedUserId, "test");
        when(authentication.getPrincipal()).thenReturn(jwtUserData);

        securityContext.setAuthentication(authentication);

        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
        when(chatUserRepository.findByChatIdAndUserId(chatId, authenticatedUserId)).thenReturn(Optional.empty());
        assertThrows(ForbiddenException.class, () -> correspondenceService.getCorrespondenceInfo(chatId));
    }

    @Test
    void getChatMessages_UserNotInChat_ThrowsForbiddenException() {
        UUID chatId = UUID.randomUUID();
        UUID authenticatedUserId = UUID.randomUUID();
        ChatEntity chat = new ChatEntity();
        chat.setId(chatId);
        chat.setChatType(ChatType.CHAT);
        chat.setName("Test Chat");
        chat.setAvatarId(UUID.randomUUID());
        chat.setAdminId(UUID.randomUUID());
        chat.setCreationDate(LocalDate.now());
        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
        when(chatUserRepository.findByChatIdAndUserId(chatId, authenticatedUserId)).thenReturn(Optional.empty());

        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = mock(Authentication.class);

        JwtUserData jwtUserData = new JwtUserData("gsagvxcvz", authenticatedUserId, "test");
        when(authentication.getPrincipal()).thenReturn(jwtUserData);

        securityContext.setAuthentication(authentication);

        assertThrows(ForbiddenException.class, () -> correspondenceService.viewCorrespondence(chatId));
    }

}
