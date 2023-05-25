package ru.hits.messengerapi.chat.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.hits.messengerapi.chat.dto.CreateChatDto;
import ru.hits.messengerapi.chat.entity.ChatEntity;
import ru.hits.messengerapi.chat.enumeration.ChatType;
import ru.hits.messengerapi.chat.service.IntegrationRequestsService;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Класс-маппер, отвечающий за преобразование объектов типа {@link CreateChatDto} и {@link UUID}
 * в объекты типа {@link ChatEntity } и наоборот.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ChatMapper {

    private final IntegrationRequestsService integrationRequestsService;

    /**
     * Преобразует объект типа {@link CreateChatDto} и {@link UUID} в объект типа {@link ChatEntity},
     * устанавливая атрибуты чата соответствующими значениями из переданных параметров.
     *
     * @param createChatDto объект типа {@link CreateChatDto}, содержащий данные для создания чата.
     * @param adminId идентификатор пользователя, являющегося администратором чата.
     * @return объект типа {@link ChatEntity} с заполненными атрибутами из переданных параметров.
     */
    public ChatEntity createChatDtoToChat(CreateChatDto createChatDto, UUID adminId) {
        log.info("Преобразование объекта CreateChatDto и UUID в объект ChatEntity: createChatDto={}, adminId={}",
                createChatDto, adminId);
        if (createChatDto.getAvatar() != null) {
            integrationRequestsService.checkAvatarIdExistence(createChatDto.getAvatar());
        }
        return ChatEntity
                .builder()
                .chatType(ChatType.CHAT)
                .name(createChatDto.getName())
                .adminId(adminId)
                .creationDate(LocalDate.now())
                .avatarId(createChatDto.getAvatar())
                .build();
    }

    /**
     * Преобразует два объекта типа {@link UUID} в объект типа {@link ChatEntity}, представляющий
     * диалог между пользователями с указанными идентификаторами. Если переданные
     * идентификаторы совпадают, создается диалог "Избранное".
     *
     * @param senderId идентификатор отправителя сообщения.
     * @param receiverId идентификатор получателя сообщения.
     * @return объект типа {@link ChatEntity}, представляющий диалог между пользователями
     *         с указанными идентификаторами.
     */
    public ChatEntity senderIdAndreceiverIdToChat(UUID senderId, UUID receiverId) {
        log.info("Преобразование двух UUID в объект ChatEntity: senderId={}, receiverId={}", senderId, receiverId);
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
