package ru.hits.messengerapi.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.hits.messengerapi.chat.dto.AttachmentDto;
import ru.hits.messengerapi.chat.dto.ChatMessageDto;
import ru.hits.messengerapi.chat.dto.DialogueMessageDto;
import ru.hits.messengerapi.chat.entity.AttachmentEntity;
import ru.hits.messengerapi.chat.entity.ChatEntity;
import ru.hits.messengerapi.chat.entity.MessageEntity;
import ru.hits.messengerapi.chat.repository.AttachmentRepository;
import ru.hits.messengerapi.chat.repository.ChatRepository;
import ru.hits.messengerapi.chat.repository.ChatUserRepository;
import ru.hits.messengerapi.chat.repository.MessageRepository;
import ru.hits.messengerapi.common.exception.ConflictException;
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

    @Transactional
    public void sendMessageToDialogue(DialogueMessageDto dialogueMessageDto) {
        integrationRequestsService.checkUserExistence(dialogueMessageDto.getReceiverId());

        UUID senderId = getAuthenticatedUserId();
        UUID receiverId = dialogueMessageDto.getReceiverId();
        integrationRequestsService.checkExistenceInFriends(senderId, receiverId);

        Optional<ChatEntity> chat = chatRepository.findBySenderIdAndReceiverId(senderId, receiverId);
        if (chat.isEmpty()) {
            ChatEntity newChat = chatService.createDialogue(receiverId);
            chat = Optional.ofNullable(newChat);
        }

        MessageEntity message = MessageEntity
                .builder()
                .chat(chat.get())
                .sendDate(LocalDateTime.now())
                .messageText(dialogueMessageDto.getMessageText())
                .senderId(senderId)
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
    }

    @Transactional
    public void sendMessageToChat(ChatMessageDto chatMessageDto) {
        Optional<ChatEntity> chat = chatRepository.findById(chatMessageDto.getChatId());
        if (chat.isEmpty()) {
            throw new NotFoundException("Чата с ID " + chatMessageDto.getChatId() + " не существует.");
        }

        UUID senderId = getAuthenticatedUserId();
        if (chatUserRepository.findByChatIdAndUserId(chatMessageDto.getChatId(), senderId).isEmpty()) {
            throw new ConflictException("Пользователь с ID " + senderId
                    + " не может отправить сообщение в чат с ID " + chatMessageDto.getChatId()
                    + ", потому что не состоит в нём.");
        }

        MessageEntity message = MessageEntity
                .builder()
                .chat(chat.get())
                .sendDate(LocalDateTime.now())
                .messageText(chatMessageDto.getMessageText())
                .senderId(senderId)
                .build();
        messageRepository.save(message);

        System.out.println(chatMessageDto.getAttachments());
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
