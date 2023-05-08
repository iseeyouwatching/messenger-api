package ru.hits.messengerapi.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final IntegrationRequestsService integrationRequestsService;

    @Transactional
    public void createChat(CreateChatDto createChatDto) {
        UUID authenticatedUserId = getAuthenticatedUserId();

        if (createChatDto.getUsers().contains(authenticatedUserId)) {
            throw new ConflictException("Пользователь не может добавить самого себя в чат.");
        }

        integrationRequestsService.checkExistenceMultiUsersInFriends(authenticatedUserId, createChatDto.getUsers());
        ChatEntity chat = chatMapper.createChatDtoToChat(createChatDto, authenticatedUserId);
        chat = chatRepository.save(chat);
        List<UUID> listOfIDs = createChatDto.getUsers();
        listOfIDs.add(authenticatedUserId);
        List<ChatUserEntity> chatUserEntityList =
                chatUserMapper.chatAndUserIdToListOfChatAndUser(chat.getId(), listOfIDs);
        chatUserRepository.saveAll(chatUserEntityList);
    }

    @Transactional
    public ChatEntity createDialogue(UUID receiverId) {
        UUID authenticatedUserId = getAuthenticatedUserId();

        if (chatRepository.findBySenderIdAndReceiverId(authenticatedUserId, receiverId).isPresent()) {
            throw new ConflictException("Пользователь не может создать больше одного диалога с самим собой.");
        }

        ChatEntity chat = chatMapper.senderIdAndreceiverIdToChat(authenticatedUserId, receiverId);
        chat = chatRepository.save(chat);

        List<UUID> listOfIDs = new ArrayList<>();
        if (authenticatedUserId.compareTo(receiverId) == 0) {
            listOfIDs.add(receiverId);
        }
        else {
            listOfIDs.add(receiverId);
            listOfIDs.add(authenticatedUserId);
        }

        List<ChatUserEntity> chatUserEntityList =
                chatUserMapper.chatAndUserIdToListOfChatAndUser(chat.getId(), listOfIDs);
        chatUserRepository.saveAll(chatUserEntityList);

        return chat;
    }

    @Transactional
    public void updateChat(UpdateChatDto updateChatDto) {
        Optional<ChatEntity> chat = chatRepository.findById(updateChatDto.getId());
        if (chat.isEmpty()) {
            throw new NotFoundException("Чата с ID " + updateChatDto.getId() + " не существует.");
        }

        UUID authenticatedUserId = getAuthenticatedUserId();
        if (updateChatDto.getUsers().contains(authenticatedUserId)) {
            throw new ConflictException("Пользователь не может добавить самого себя в чат.");
        }
        integrationRequestsService.checkExistenceMultiUsersInFriends(authenticatedUserId, updateChatDto.getUsers());


        if (updateChatDto.getName() != null) {
            chat.get().setName(updateChatDto.getName());
        }

        chat.get().setAvatarId(updateChatDto.getAvatar());

        if (updateChatDto.getUsers() != null) {
            List<UUID> listOfIDs = updateChatDto.getUsers();
            listOfIDs.add(authenticatedUserId);
            List<ChatUserEntity> chatUserEntityList =
                    chatUserMapper.chatAndUserIdToListOfChatAndUser(chat.get().getId(), listOfIDs);
            chatUserRepository.deleteAllByChatIdAndUserIdNotIn(chat.get().getId(), updateChatDto.getUsers());
            for (ChatUserEntity chatUser : chatUserEntityList) {
                if (chatUserRepository.findByChatIdAndUserId(chat.get().getId(), chatUser.getUserId()).isEmpty()) {
                    chatUserRepository.save(chatUser);
                }
            }
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
