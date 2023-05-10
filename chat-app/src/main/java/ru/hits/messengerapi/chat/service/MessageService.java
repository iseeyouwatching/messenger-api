package ru.hits.messengerapi.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.hits.messengerapi.chat.dto.*;
import ru.hits.messengerapi.chat.entity.AttachmentEntity;
import ru.hits.messengerapi.chat.entity.ChatEntity;
import ru.hits.messengerapi.chat.entity.MessageEntity;
import ru.hits.messengerapi.chat.entity.ChatUserEntity;
import ru.hits.messengerapi.chat.enumeration.ChatType;
import ru.hits.messengerapi.chat.repository.AttachmentRepository;
import ru.hits.messengerapi.chat.repository.ChatRepository;
import ru.hits.messengerapi.chat.repository.ChatUserRepository;
import ru.hits.messengerapi.chat.repository.MessageRepository;
import ru.hits.messengerapi.common.dto.NewNotificationDto;
import ru.hits.messengerapi.common.enumeration.NotificationType;
import ru.hits.messengerapi.common.exception.ForbiddenException;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.common.security.JwtUserData;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис сообщений.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    /**
     * Репозиторий для работы с сущностью {@link MessageEntity}.
     */
    private final MessageRepository messageRepository;

    /**
     * Репозиторий для работы с сущностью {@link AttachmentEntity}.
     */
    private final AttachmentRepository attachmentRepository;

    /**
     * Репозиторий для работы с сущностью {@link ChatEntity}.
     */
    private final ChatRepository chatRepository;

    /**
     * Репозиторий для работы с сущностью {@link ChatUserEntity}.
     */
    private final ChatUserRepository chatUserRepository;

    /**
     * Сервис чатов.
     */
    private final ChatService chatService;

    /**
     * Сервис интеграционных запросов.
     */
    private final IntegrationRequestsService integrationRequestsService;

    /**
     * Объект, позволяющий отправлять сообщения в RabbitMQ, используя Spring Cloud Stream.
     */
    private final StreamBridge streamBridge;

    /**
     * Метод для отправки сообщения в диалог.
     *
     * @param dialogueMessageDto DTO с информацией о сообщении, которое отправится в диалог.
     */
    @Transactional
    public void sendMessageToDialogue(DialogueMessageDto dialogueMessageDto) {
        integrationRequestsService.checkUserExistence(dialogueMessageDto.getReceiverId());

        UUID senderId = getAuthenticatedUserId();
        UUID receiverId = dialogueMessageDto.getReceiverId();

        if (senderId.compareTo(receiverId) != 0) {
            integrationRequestsService.checkExistenceInFriends(senderId, receiverId);
        }

        Optional<ChatEntity> chat = chatRepository.findBySenderIdAndReceiverId(senderId, receiverId);
        if (chat.isEmpty()) {
            ChatEntity newChat = chatService.createDialogue(receiverId);
            chat = Optional.ofNullable(newChat);
        }

        List<String> fullNameAndAvatarId = integrationRequestsService.getFullNameAndAvatarId(senderId);
        String avatarIdString = fullNameAndAvatarId.get(1);
        UUID senderAvatarId = null;
        if (avatarIdString != null) {
            senderAvatarId = UUID.fromString(avatarIdString);
        }
        MessageEntity message = MessageEntity
                .builder()
                .chat(chat.get())
                .sendDate(LocalDateTime.now())
                .messageText(dialogueMessageDto.getMessageText())
                .senderId(senderId)
                .senderName(fullNameAndAvatarId.get(0))
                .senderAvatarId(senderAvatarId)
                .build();
        messageRepository.save(message);

        if (dialogueMessageDto.getAttachments() != null) {
            List<AttachmentEntity> attachments = new ArrayList<>();
            for (UUID attachmentId: dialogueMessageDto.getAttachments()) {
                    AttachmentEntity attachment = AttachmentEntity.builder()
                            .message(message)
                            .fileId(attachmentId)
                            .fileName(null)
                            .build();
                    attachments.add(attachment);
            }
            attachmentRepository.saveAll(attachments);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDateTime = LocalDateTime.now().format(formatter);

        NewNotificationDto newNotificationDto = NewNotificationDto.builder()
                .userId(receiverId)
                .type(NotificationType.MESSAGE)
                .text("Поступило новое личное сообщение от пользователя с ID " + senderId
                        + " и ФИО " + fullNameAndAvatarId.get(0) + "."
                        + " Дата и время отправки сообщения: " + formattedDateTime + "." + " Часть сообщения: '"
                        + dialogueMessageDto.getMessageText()
                        .substring(0, Math.min(dialogueMessageDto.getMessageText().length(), 100)) + "'.")
                .build();
        sendByStreamBridge(newNotificationDto);
    }

    /**
     * Метод для отправки сообщения в чат.
     *
     * @param chatMessageDto DTO с информацией о сообщении, которое отправится в чат.
     * @throws NotFoundException если чата не существует.
     * @throws ForbiddenException если пользователь пытается отправить сообщение в чат, но не состоит в нём.
     */
    @Transactional
    public void sendMessageToChat(ChatMessageDto chatMessageDto) {
        Optional<ChatEntity> chat = chatRepository.findById(chatMessageDto.getChatId());
        if (chat.isEmpty()) {
            throw new NotFoundException("Чата с ID " + chatMessageDto.getChatId() + " не существует.");
        }

        UUID senderId = getAuthenticatedUserId();
        if (chatUserRepository.findByChatIdAndUserId(chatMessageDto.getChatId(), senderId).isEmpty()) {
            throw new ForbiddenException("Пользователь с ID " + senderId
                    + " не может отправить сообщение в чат с ID " + chatMessageDto.getChatId()
                    + ", потому что не состоит в нём.");
        }

        List<String> fullNameAndAvatarId = integrationRequestsService.getFullNameAndAvatarId(senderId);
        String avatarIdString = fullNameAndAvatarId.get(1);
        UUID senderAvatarId = null;
        if (avatarIdString != null) {
            senderAvatarId = UUID.fromString(avatarIdString);
        }
        MessageEntity message = MessageEntity
                .builder()
                .chat(chat.get())
                .sendDate(LocalDateTime.now())
                .messageText(chatMessageDto.getMessageText())
                .senderId(senderId)
                .senderName(fullNameAndAvatarId.get(0))
                .senderAvatarId(senderAvatarId)
                .build();
        messageRepository.save(message);

        if (chatMessageDto.getAttachments() != null) {
            List<AttachmentEntity> attachments = new ArrayList<>();
            for (UUID attachmentId: chatMessageDto.getAttachments()) {
                AttachmentEntity attachment = AttachmentEntity.builder()
                        .message(message)
                        .fileId(attachmentId)
                        .fileName(null)
                        .build();
                attachments.add(attachment);
            }
            attachmentRepository.saveAll(attachments);
        }
    }

    /**
     * Метод для поиска сообщений.
     *
     * @param searchStringDto поисковая строка.
     * @return список найденных сообщений.
     */
    public List<MessageDto> searchMessages(SearchStringDto searchStringDto) {
        UUID authenticatedUserId = getAuthenticatedUserId();
        List<MessageEntity> messages;
        if (searchStringDto.getSearchString() != null) {
            messages = messageRepository.searchMessages(
                    searchStringDto.getSearchString().toLowerCase(), authenticatedUserId);
        }
        else {
            messages = messageRepository.searchMessagesWithoutFilter(authenticatedUserId);
        }

        List<MessageDto> searchResults = new ArrayList<>();
        for (MessageEntity message : messages) {
            MessageDto searchResult = new MessageDto();
            searchResult.setChatId(message.getChat().getId());
            if (message.getChat().getChatType().equals(ChatType.CHAT)) {
                searchResult.setChatName(message.getChat().getName());
            }
            else if (message.getChat().getChatType().equals(ChatType.DIALOGUE)) {
                if (authenticatedUserId.compareTo(message.getChat().getReceiverId()) != 0) {
                    String fullName = integrationRequestsService
                            .getFullNameAndAvatarId(message.getChat().getReceiverId()).get(0);
                    searchResult.setChatName(fullName);
                } else if (authenticatedUserId.compareTo(message.getChat().getSenderId()) != 0) {
                    String fullName = integrationRequestsService
                            .getFullNameAndAvatarId(message.getChat().getSenderId()).get(0);
                    searchResult.setChatName(fullName);
                }
                else {
                    searchResult.setChatName(message.getChat().getName());
                }
            }
            searchResult.setMessageText(message.getMessageText());
            searchResult.setMessageSendDate(message.getSendDate());

            List<String> attachmentNames = new ArrayList<>();
            for (AttachmentEntity attachment : attachmentRepository.findAllByMessageId(message.getId())) {
                attachmentNames.add(attachment.getFileName());
            }
            searchResult.setFileNames(attachmentNames);

            searchResults.add(searchResult);
        }

        return searchResults;
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

    /**
     * Отправляет объект типа NewNotificationDto посредством StreamBridge.
     *
     * @param newNotificationDto объект класса {@link NewNotificationDto},
     *                           содержащий информацию о новом уведомлении
     */
    private void sendByStreamBridge(NewNotificationDto newNotificationDto) {
        streamBridge.send("newNotificationEvent-out-0", newNotificationDto);
    }

}
