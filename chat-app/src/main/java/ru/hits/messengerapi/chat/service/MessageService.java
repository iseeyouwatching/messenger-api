package ru.hits.messengerapi.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.hits.messengerapi.chat.dto.AttachmentDto;
import ru.hits.messengerapi.chat.dto.DialogueMessageDto;
import ru.hits.messengerapi.chat.entity.AttachmentEntity;
import ru.hits.messengerapi.chat.entity.ChatEntity;
import ru.hits.messengerapi.chat.entity.ChatUserEntity;
import ru.hits.messengerapi.chat.entity.MessageEntity;
import ru.hits.messengerapi.chat.repository.AttachmentRepository;
import ru.hits.messengerapi.chat.repository.ChatRepository;
import ru.hits.messengerapi.chat.repository.ChatUserRepository;
import ru.hits.messengerapi.chat.repository.MessageRepository;
import ru.hits.messengerapi.common.security.JwtUserData;

import javax.swing.text.html.Option;
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
    private final ChatService chatService;

    public void sendMessageToDialogue(DialogueMessageDto dialogueMessageDto) {
        UUID senderId = getAuthenticatedUserId();
        UUID receiverId = dialogueMessageDto.getReceiverId();

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
