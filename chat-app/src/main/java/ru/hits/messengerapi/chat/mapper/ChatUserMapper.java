package ru.hits.messengerapi.chat.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hits.messengerapi.chat.entity.ChatUserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChatUserMapper {

    public List<ChatUserEntity> chatAndUserIdToListOfChatAndUser(UUID chatId, List<UUID> userIDs) {
        List<ChatUserEntity> chatUserEntityList = new ArrayList<>();
        for (UUID userId: userIDs) {
            chatUserEntityList.add(
                    ChatUserEntity
                    .builder()
                    .chatId(chatId)
                    .userId(userId)
                    .build()
            );
        }
        return chatUserEntityList;
    }

}
