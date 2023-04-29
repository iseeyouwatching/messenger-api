package ru.hits.messengerapi.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.hits.messengerapi.chat.dto.CreateChatDto;
import ru.hits.messengerapi.chat.entity.ChatEntity;
import ru.hits.messengerapi.chat.entity.ChatUserEntity;
import ru.hits.messengerapi.chat.mapper.ChatMapper;
import ru.hits.messengerapi.chat.mapper.ChatUserMapper;
import ru.hits.messengerapi.chat.repository.ChatRepository;
import ru.hits.messengerapi.chat.repository.ChatUserRepository;
import ru.hits.messengerapi.common.security.JwtUserData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatUserRepository chatUserRepository;
    private final ChatMapper chatMapper;
    private final ChatUserMapper chatUserMapper;
    
    public void createChat(CreateChatDto createChatDto) {
        UUID authenticatedUserId = getAuthenticatedUserId();
        ChatEntity chat = chatMapper.createChatDtoToChat(createChatDto, authenticatedUserId);
        chat = chatRepository.save(chat);
        List<UUID> listOfIDs = createChatDto.getUsers();
        listOfIDs.add(authenticatedUserId);
        List<ChatUserEntity> chatUserEntityList =
                chatUserMapper.chatAndUserIdToListOfChatAndUser(chat.getId(), listOfIDs);
        chatUserRepository.saveAll(chatUserEntityList);
    }

    /**
     * Метод для получения ID аутентифицированного пользователя.
     *
     * @return ID аутентифицированного пользователя.
     */
    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
        return userData.getId();
    }

}
