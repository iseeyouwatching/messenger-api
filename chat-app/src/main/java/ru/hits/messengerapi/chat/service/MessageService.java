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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final AttachmentRepository attachmentRepository;
    private final ChatRepository chatRepository;
    private final ChatUserRepository chatUserRepository;
    private final ChatService chatService;
    private final IntegrationRequestsService integrationRequestsService;
    private final StreamBridge streamBridge;

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
            for (AttachmentDto attachmentDto: dialogueMessageDto.getAttachments()) {
                if (attachmentDto.getFileName() != null && attachmentDto.getFileId() != null) {
                    AttachmentEntity attachment = AttachmentEntity
                            .builder()
                            .message(message)
                            .fileId(attachmentDto.getFileId())
                            .fileName(attachmentDto.getFileName())
                            .build();
                    attachments.add(attachment);
                }
            }
            attachmentRepository.saveAll(attachments);
        }
        NewNotificationDto newNotificationDto = NewNotificationDto.builder()
                .userId(receiverId)
                .type(NotificationType.MESSAGE)
                .text("Поступило новое личное сообщение от пользователя с ID " + senderId
                        + " и ФИО " + fullNameAndAvatarId.get(0) + ".")
                .build();
        sendByStreamBridge(newNotificationDto);
    }

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
            for (AttachmentDto attachmentDto: chatMessageDto.getAttachments()) {
                if (attachmentDto.getFileName() != null && attachmentDto.getFileId() != null) {
                    AttachmentEntity attachment = AttachmentEntity
                            .builder()
                            .message(message)
                            .fileId(attachmentDto.getFileId())
                            .fileName(attachmentDto.getFileName())
                            .build();
                    attachments.add(attachment);
                }
            }
            attachmentRepository.saveAll(attachments);
        }
    }

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

    private void sendByStreamBridge(NewNotificationDto newNotificationDto) {
        streamBridge.send("newNotificationEvent-out-0", newNotificationDto);
    }

}
