package ru.hits.messengerapi.chat.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hits.messengerapi.chat.dto.CreateChatDto;
import ru.hits.messengerapi.chat.entity.ChatEntity;
import ru.hits.messengerapi.chat.enumeration.ChatType;

import java.time.LocalDate;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChatMapper {

    public ChatEntity createChatDtoToChat(CreateChatDto createChatDto, UUID adminId) {
        return ChatEntity
                .builder()
                .chatType(ChatType.CHAT)
                .name(createChatDto.getName())
                .adminId(adminId)
                .creationDate(LocalDate.now())
                .avatarId(createChatDto.getAvatar())
                .build();
    }

    public ChatEntity senderIdAndreceiverIdToChat(UUID senderId, UUID receiverId) {
        if (senderId.compareTo(receiverId) != 0) {
            return ChatEntity
                    .builder()
                    .chatType(ChatType.DIALOGUE)
                    .creationDate(LocalDate.now())
                    .senderId(senderId)
                    .receiverId(receiverId)
                    .build();
        }
        else {
            return ChatEntity
                    .builder()
                    .chatType(ChatType.DIALOGUE)
                    .name("Избранное")
                    .creationDate(LocalDate.now())
                    .senderId(senderId)
                    .receiverId(receiverId)
                    .build();
        }
    }
}
