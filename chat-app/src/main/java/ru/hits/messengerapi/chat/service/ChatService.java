package ru.hits.messengerapi.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.hits.messengerapi.chat.dto.CreateChatDto;
import ru.hits.messengerapi.chat.dto.UpdateChatDto;
import ru.hits.messengerapi.chat.entity.ChatEntity;
import ru.hits.messengerapi.chat.entity.ChatUserEntity;
import ru.hits.messengerapi.chat.mapper.ChatMapper;
import ru.hits.messengerapi.chat.mapper.ChatUserMapper;
import ru.hits.messengerapi.chat.repository.ChatRepository;
import ru.hits.messengerapi.chat.repository.ChatUserRepository;
import ru.hits.messengerapi.common.exception.ConflictException;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.common.security.JwtUserData;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatUserRepository chatUserRepository;
    private final ChatMapper chatMapper;
    private final ChatUserMapper chatUserMapper;

    @Transactional
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

    @Transactional
    public void updateChat(UpdateChatDto updateChatDto) {
        UUID authenticatedUserId = getAuthenticatedUserId();
        Optional<ChatEntity> chat = chatRepository.findById(updateChatDto.getId());

        if (chat.isEmpty()) {
            throw new NotFoundException("Чата с ID " + updateChatDto.getId() + " не существует.");
        }

        if (updateChatDto.getName() != null) {
            chat.get().setName(updateChatDto.getName());
        }

        if (updateChatDto.getAvatar() != null) {
            chat.get().setAvatarId(updateChatDto.getAvatar());
        }

        if (updateChatDto.getUsers() != null) {
            if (!updateChatDto.getUsers().contains(authenticatedUserId)) {
                throw new ConflictException("Админа с ID " + authenticatedUserId + " нет в чате, который создан им.");
            }
            List<UUID> listOfIDs = updateChatDto.getUsers();
            List<ChatUserEntity> chatUserEntityList =
                    chatUserMapper.chatAndUserIdToListOfChatAndUser(chat.get().getId(), listOfIDs);
            chatUserRepository.deleteAllByChatId(chat.get().getId());
            chatUserRepository.saveAll(chatUserEntityList);
        }

        chatRepository.save(chat.get());
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
