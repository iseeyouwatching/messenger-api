package ru.hits.messengerapi.chat.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hits.messengerapi.chat.entity.ChatUserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Класс-маппер для преобразования данных о чате и идентификаторах пользователей в
 * список сущностей "чат-пользователь".
 */
@Component
@RequiredArgsConstructor
public class ChatUserMapper {

    /**
     * Преобразует идентификаторы пользователей и идентификатор чата в список сущностей "чат-пользователь".
     *
     * @param chatId идентификатор чата.
     * @param userIDs список идентификаторов пользователей.
     * @return список сущностей "чат-пользователь".
     */
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
