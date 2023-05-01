package ru.hits.messengerapi.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.hits.messengerapi.chat.dto.CorrespondenceInfoDto;
import ru.hits.messengerapi.chat.dto.MessageInCorrespondenceDto;
import ru.hits.messengerapi.chat.entity.ChatEntity;
import ru.hits.messengerapi.chat.entity.MessageEntity;
import ru.hits.messengerapi.chat.repository.ChatRepository;
import ru.hits.messengerapi.chat.repository.ChatUserRepository;
import ru.hits.messengerapi.chat.repository.MessageRepository;
import ru.hits.messengerapi.common.exception.ForbiddenException;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.common.security.JwtUserData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CorrespondenceService {

    private final ChatRepository chatRepository;
    private final ChatUserRepository chatUserRepository;
    private final MessageRepository messageRepository;

    public CorrespondenceInfoDto getCorrespondenceInfo(UUID id) {
        Optional<ChatEntity> chat = chatRepository.findById(id);
        if (chat.isEmpty()) {
            throw new NotFoundException("Переписки с ID " + id + " не существует.");
        }

        if (chatUserRepository.findByChatIdAndUserId(id, getAuthenticatedUserId()).isEmpty()) {
            throw new ForbiddenException("Пользователь с ID " + getAuthenticatedUserId()
                    + " не может посмотреть информацию о переписке с ID " + id
                    + ", потому что не состоит в ней.");
        }

        return new CorrespondenceInfoDto(
                chat.get().getName(),
                chat.get().getAvatarId(),
                chat.get().getAdminId(),
                chat.get().getCreationDate()
        );
    }

    public List<MessageInCorrespondenceDto> viewCorrespondence(UUID id) {
        Optional<ChatEntity> chat = chatRepository.findById(id);
        if (chat.isEmpty()) {
            throw new NotFoundException("Переписки с ID " + id + " не существует.");
        }

        if (chatUserRepository.findByChatIdAndUserId(id, getAuthenticatedUserId()).isEmpty()) {
            throw new ForbiddenException("Пользователь с ID " + getAuthenticatedUserId()
                    + " не может посмотреть информацию о переписке с ID " + id
                    + ", потому что не состоит в ней.");
        }

        List<MessageEntity> messages = messageRepository.findAllByChatId(id);
        List<MessageInCorrespondenceDto> messageInCorrespondenceDtos = new ArrayList<>();
        for (MessageEntity message: messages) {
            messageInCorrespondenceDtos.add(new MessageInCorrespondenceDto(message));
        }

        return messageInCorrespondenceDtos;
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
