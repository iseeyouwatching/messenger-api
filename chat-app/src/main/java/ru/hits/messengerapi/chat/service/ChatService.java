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
import ru.hits.messengerapi.common.exception.ForbiddenException;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.common.security.JwtUserData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис чатов.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    /**
     * Репозиторий для работы с сущностью {@link ChatEntity}.
     */
    private final ChatRepository chatRepository;

    /**
     * Репозиторий для работы с сущностью {@link ChatUserEntity}.
     */
    private final ChatUserRepository chatUserRepository;

    /**
     * Класс-маппер, отвечающий за преобразование объектов типа {@link CreateChatDto} и {@link UUID}
     * в объекты типа {@link ChatEntity } и наоборот.
     */
    private final ChatMapper chatMapper;

    /**
     * Класс-маппер для преобразования данных о чате и идентификаторах пользователей в
     * список сущностей "чат-пользователь".
     */
    private final ChatUserMapper chatUserMapper;

    /**
     * Сервис интеграционных запросов.
     */
    private final IntegrationRequestsService integrationRequestsService;

    /**
     * Метод для создания нового чата на основе переданных данных из объекта {@link CreateChatDto}.
     * Если чат успешно создан, добавляет всех пользователей из списка в базу данных и
     * связывает их с созданным чатом.
     *
     * @param createChatDto объект, содержащий данные для создания нового чата.
     * @throws ConflictException если в списке пользователей есть идентификатор текущего
     * аутентифицированного пользователя.
     */
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
        log.debug("Создан новый чат с ID: {}", chat.getId());
    }


    /**
     * Метод для создания нового диалога с пользователем, идентификатор которого передан в качестве параметра
     * {@code receiverId}. Если диалог уже существует, выбрасывает исключение {@link ConflictException}.
     *
     * @param receiverId идентификатор пользователя, с которым создается диалог.
     * @return объект {@link ChatEntity}, созданный на основе переданных данных.
     * @throws ConflictException если текущий пользователь пытается создать больше одного диалога с самим собой.
     */
    @Transactional
    public ChatEntity createDialogue(UUID receiverId) {
        UUID authenticatedUserId = getAuthenticatedUserId();

        if (chatRepository.findBySenderIdAndReceiverId(authenticatedUserId, receiverId).isPresent()) {
            log.warn("Пользователь не может создать больше одного диалога с самим собой.");
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

        log.info("Создан диалог между пользователями с ID {} и {}.", authenticatedUserId, receiverId);
        return chat;
    }


    /**
     * Метод для обновления данных чата.
     *
     * @param updateChatDto DTO-объект с данными, которые необходимо изменить.
     * @throws NotFoundException если чат с указанным ID не найден.
     * @throws ForbiddenException если пользователь не является членом чата и не может изменить его данные.
     * @throws ConflictException если пользователь пытается добавить самого себя в чат.
     */
    @Transactional
    public void updateChat(UpdateChatDto updateChatDto) {
        Optional<ChatEntity> chat = chatRepository.findById(updateChatDto.getId());
        if (chat.isEmpty()) {
            throw new NotFoundException("Чата с ID " + updateChatDto.getId() + " не существует.");
        }

        UUID authenticatedUserId = getAuthenticatedUserId();
        if (chatUserRepository.findByChatIdAndUserId(updateChatDto.getId(), authenticatedUserId).isEmpty()) {
            throw new ForbiddenException("Пользователь с ID " + authenticatedUserId
                    + " не может изменить данные чата с ID " + updateChatDto.getId()
                    + ", так как не состоит в нём.");
        }

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
            if (authenticatedUserId.compareTo(chat.get().getAdminId()) == 0) {
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
            else {
                List<ChatUserEntity> chatUserEntityList =
                        chatUserMapper.chatAndUserIdToListOfChatAndUser(chat.get().getId(), listOfIDs);
                for (ChatUserEntity chatUser : chatUserEntityList) {
                    if (chatUserRepository.findByChatIdAndUserId(chat.get().getId(), chatUser.getUserId()).isEmpty()) {
                        chatUserRepository.save(chatUser);
                    }
                }
            }
        }
        chatRepository.save(chat.get());
        log.info("Чат с ID {} был обновлен.", updateChatDto.getId());
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
