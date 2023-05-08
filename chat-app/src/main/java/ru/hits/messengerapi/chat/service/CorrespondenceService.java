package ru.hits.messengerapi.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.hits.messengerapi.chat.dto.CorrespondenceInfoDto;
import ru.hits.messengerapi.chat.dto.MessageInCorrespondenceDto;
import ru.hits.messengerapi.chat.dto.PaginationCorrespondancesDto;
import ru.hits.messengerapi.chat.dto.PaginationWithChatNameDto;
import ru.hits.messengerapi.chat.entity.ChatEntity;
import ru.hits.messengerapi.chat.entity.ChatUserEntity;
import ru.hits.messengerapi.chat.entity.MessageEntity;
import ru.hits.messengerapi.chat.enumeration.ChatType;
import ru.hits.messengerapi.chat.repository.ChatRepository;
import ru.hits.messengerapi.chat.repository.ChatUserRepository;
import ru.hits.messengerapi.chat.repository.MessageRepository;
import ru.hits.messengerapi.common.exception.ForbiddenException;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.common.helpingservices.implementation.CheckPaginationInfoService;
import ru.hits.messengerapi.common.security.JwtUserData;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CorrespondenceService {

    private final ChatRepository chatRepository;
    private final ChatUserRepository chatUserRepository;
    private final MessageRepository messageRepository;
    private final IntegrationRequestsService integrationRequestsService;
    private final CheckPaginationInfoService checkPaginationInfoService;

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

        integrationRequestsService.syncUserData(getAuthenticatedUserId());
        List<MessageEntity> messages = messageRepository.findAllByChatId(id);
        List<MessageInCorrespondenceDto> messageInCorrespondenceDtos = new ArrayList<>();
        for (MessageEntity message: messages) {
            messageInCorrespondenceDtos.add(new MessageInCorrespondenceDto(message));
        }

        Comparator<MessageInCorrespondenceDto> sortByDate =
                Comparator.comparing(
                        dto -> dto.getSendDate() != null ? dto.getSendDate() : LocalDateTime.MIN,
                        Comparator.reverseOrder()
                );
        messageInCorrespondenceDtos.sort(sortByDate);

        return messageInCorrespondenceDtos;
    }

    public List<PaginationCorrespondancesDto> getCorrespondences(PaginationWithChatNameDto
                                                                         paginationWithChatNameDto) {
        int pageNumber = paginationWithChatNameDto.getPageInfo().getPageNumber();
        int pageSize = paginationWithChatNameDto.getPageInfo().getPageSize();
        checkPaginationInfoService.checkPagination(pageNumber, pageSize);
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        UUID authenticatedUserId = getAuthenticatedUserId();

        List<ChatUserEntity> chatUserEntities = chatUserRepository.findByUserId(authenticatedUserId, pageable);
        List<ChatEntity> chatEntities = new ArrayList<>();
        for (ChatUserEntity chatUser: chatUserEntities) {
            chatRepository.findById(chatUser.getChatId()).ifPresent(chat -> {
                if (paginationWithChatNameDto.getChatName() == null) {
                    chatEntities.add(chat);
                }
                else {
                    if (chat.getChatType().equals(ChatType.CHAT) && chat.getName().toLowerCase().contains(
                            paginationWithChatNameDto.getChatName().toLowerCase())) {
                        chatEntities.add(chat);
                    }
                    else if (chat.getChatType().equals(ChatType.DIALOGUE))
                    {
                        if (authenticatedUserId == chat.getReceiverId()) {
                            if (chat.getName().toLowerCase().contains(
                                    paginationWithChatNameDto.getChatName().toLowerCase())) {
                                chatEntities.add(chat);
                            }
                        }
                        else {
                            String fullName = integrationRequestsService
                                    .getFullNameAndAvatarId(chat.getReceiverId()).get(0);
                            if (fullName.toLowerCase().contains(
                                    paginationWithChatNameDto.getChatName().toLowerCase())) {
                                chatEntities.add(chat);
                            }
                        }
                    }
                }
            });
        }

        List<PaginationCorrespondancesDto> result = new ArrayList<>();
        for (ChatEntity chat: chatEntities) {
            MessageEntity message = messageRepository.getFirstByChatOrderBySendDateDesc(chat);
            if (message == null) {
                if (chat.getChatType().equals(ChatType.CHAT)) {
                    result.add(new PaginationCorrespondancesDto(
                            chat.getId(),
                            chat.getName(),
                            null,
                            null,
                            null
                    ));
                }
                else if (chat.getChatType().equals(ChatType.DIALOGUE)) {
                    if (authenticatedUserId.compareTo(chat.getReceiverId()) != 0) {
                        String fullName = integrationRequestsService.getFullNameAndAvatarId(chat.getReceiverId()).get(0);
                        result.add(new PaginationCorrespondancesDto(
                                chat.getId(),
                                fullName,
                                null,
                                null,
                                null
                        ));
                    } else {
                        result.add(new PaginationCorrespondancesDto(
                                chat.getId(),
                                chat.getName(),
                                null,
                                null,
                                null
                        ));
                    }

                }
            }
            else {
                if (chat.getChatType().equals(ChatType.CHAT)) {
                    result.add(new PaginationCorrespondancesDto(
                            chat.getId(),
                            chat.getName(),
                            message.getMessageText(),
                            message.getSendDate(),
                            message.getSenderId()
                    ));
                }
                else if (chat.getChatType().equals(ChatType.DIALOGUE)) {
                    if (authenticatedUserId.compareTo(chat.getReceiverId()) != 0) {
                        String fullName = integrationRequestsService.getFullNameAndAvatarId(chat.getReceiverId()).get(0);
                        result.add(new PaginationCorrespondancesDto(
                                chat.getId(),
                                fullName,
                                message.getMessageText(),
                                message.getSendDate(),
                                message.getSenderId()
                        ));
                    } else {
                        result.add(new PaginationCorrespondancesDto(
                                chat.getId(),
                                chat.getName(),
                                message.getMessageText(),
                                message.getSendDate(),
                                message.getSenderId()
                        ));
                    }
                }
            }
        }

        Comparator<PaginationCorrespondancesDto> sortByDate =
                Comparator.comparing(
                        dto -> dto.getLastMessageSendDate() != null ? dto.getLastMessageSendDate() : LocalDateTime.MIN,
                        Comparator.reverseOrder()
                );
        result.sort(sortByDate);

        return result;
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
