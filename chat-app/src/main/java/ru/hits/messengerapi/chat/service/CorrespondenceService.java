package ru.hits.messengerapi.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.hits.messengerapi.chat.dto.*;
import ru.hits.messengerapi.chat.entity.ChatEntity;
import ru.hits.messengerapi.chat.entity.ChatUserEntity;
import ru.hits.messengerapi.chat.entity.MessageEntity;
import ru.hits.messengerapi.chat.enumeration.ChatType;
import ru.hits.messengerapi.chat.repository.ChatRepository;
import ru.hits.messengerapi.chat.repository.ChatUserRepository;
import ru.hits.messengerapi.chat.repository.MessageRepository;
import ru.hits.messengerapi.common.dto.PageInfoDto;
import ru.hits.messengerapi.common.exception.ForbiddenException;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.common.helpingservices.CheckPaginationInfoService;
import ru.hits.messengerapi.common.security.JwtUserData;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Сервис переписок.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CorrespondenceService {

    /**
     * Репозиторий для работы с сущностью {@link ChatEntity}.
     */
    private final ChatRepository chatRepository;

    /**
     * Репозиторий для работы с сущностью {@link ChatUserEntity}.
     */
    private final ChatUserRepository chatUserRepository;

    /**
     * Репозиторий для работы с сущностью {@link MessageEntity}.
     */
    private final MessageRepository messageRepository;

    /**
     * Сервис интеграционных запросов.
     */
    private final IntegrationRequestsService integrationRequestsService;

    /**
     * Сервис для проверки данных пагинации.
     */
    private final CheckPaginationInfoService checkPaginationInfoService;

    /**
     * Получает информацию о переписке.
     *
     * @param id идентификатор переписки.
     * @throws NotFoundException если переписки не существует.
     * @throws ForbiddenException если пользователь не состоит в переписке и пытается посмотреть данные о ней.
     * @return информация о переписке.
     */
    public CorrespondenceInfoDto getCorrespondenceInfo(UUID id) {
        log.info("Получение информации о переписке с ID {}", id);
        Optional<ChatEntity> chat = chatRepository.findById(id);
        if (chat.isEmpty()) {
            throw new NotFoundException("Переписки с ID " + id + " не существует.");
        }

        if (chatUserRepository.findByChatIdAndUserId(id, getAuthenticatedUserId()).isEmpty()) {
            throw new ForbiddenException("Пользователь с ID " + getAuthenticatedUserId()
                    + " не может посмотреть информацию о переписке с ID " + id
                    + ", потому что не состоит в ней.");
        }

        String chatName;
        if (getAuthenticatedUserId().compareTo(chat.get().getReceiverId()) != 0) {
            chatName = integrationRequestsService
                    .getFullNameAndAvatarId(chat.get().getReceiverId()).get(0);
        } else if (getAuthenticatedUserId().compareTo(chat.get().getSenderId()) != 0) {
            chatName = integrationRequestsService
                    .getFullNameAndAvatarId(chat.get().getSenderId()).get(0);
        }
        else {
            chatName = chat.get().getName();
        }

        log.info("Информация о переписке с ID {} получена успешно", id);
        return new CorrespondenceInfoDto(
                chatName,
                chat.get().getAvatarId(),
                chat.get().getAdminId(),
                chat.get().getCreationDate()
        );
    }

    /**
     * Метод для получения списка сообщений в переписке.
     *
     * @param id идентификатор переписки.
     * @throws NotFoundException если переписки не существует.
     * @throws ForbiddenException если пользователь не состоит в переписке и пытается посмотреть ее.
     * @return список сообщений в переписке.
     */
    public List<MessageInCorrespondenceDto> viewCorrespondence(UUID id) {
        Optional<ChatEntity> chat = chatRepository.findById(id);
        if (chat.isEmpty()) {
            String errorMessage = "Переписки с ID " + id + " не существует.";
            log.error(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        if (chatUserRepository.findByChatIdAndUserId(id, getAuthenticatedUserId()).isEmpty()) {
            String errorMessage = "Пользователь с ID " + getAuthenticatedUserId()
                    + " не может посмотреть переписку с ID " + id
                    + ", потому что не состоит в ней.";
            log.error(errorMessage);
            throw new ForbiddenException(errorMessage);
        }

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

    /**
     * Метод для постраничного получения информации о переписках.
     *
     * @param paginationWithChatNameDto DTO с информацией о пагинации и фильтре по названию чата.
     * @return постраничная инфомрация о переписках.
     */
    public CorrespondencesPageListDto getCorrespondences(PaginationWithChatNameDto
                                                                         paginationWithChatNameDto) {
        log.info("Начинаем получение переписок с параметрами {}", paginationWithChatNameDto);
        int pageNumber =paginationWithChatNameDto.getPageInfo() != null &&
                paginationWithChatNameDto.getPageInfo().getPageNumber() == null ? 1
                : paginationWithChatNameDto.getPageInfo().getPageNumber();
        int pageSize = paginationWithChatNameDto.getPageInfo() != null &&
                paginationWithChatNameDto.getPageInfo().getPageSize() == null ? 50
                : paginationWithChatNameDto.getPageInfo().getPageSize();
        checkPaginationInfoService.checkPagination(pageNumber, pageSize);
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        UUID authenticatedUserId = getAuthenticatedUserId();

        List<ChatUserEntity> chatUserEntities = chatUserRepository.findAllByUserId(authenticatedUserId, pageable);
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

        List<CorrespondenceDto> correspondenceDtos = new ArrayList<>();
        for (ChatEntity chat: chatEntities) {
            MessageEntity message = messageRepository.getFirstByChatOrderBySendDateDesc(chat);
            if (message == null) {
                if (chat.getChatType().equals(ChatType.CHAT)) {
                    correspondenceDtos.add(new CorrespondenceDto(
                            chat.getId(),
                            chat.getName(),
                            null,
                            null,
                            null
                    ));
                }
                else if (chat.getChatType().equals(ChatType.DIALOGUE)) {
                    if (authenticatedUserId.compareTo(chat.getReceiverId()) != 0) {
                        String fullName = integrationRequestsService
                                .getFullNameAndAvatarId(chat.getReceiverId()).get(0);
                        correspondenceDtos.add(new CorrespondenceDto(
                                chat.getId(),
                                fullName,
                                null,
                                null,
                                null
                        ));
                    } else if (authenticatedUserId.compareTo(chat.getSenderId()) != 0) {
                        String fullName = integrationRequestsService
                                .getFullNameAndAvatarId(chat.getSenderId()).get(0);
                        correspondenceDtos.add(new CorrespondenceDto(
                                chat.getId(),
                                fullName,
                                null,
                                null,
                                null
                        ));
                    }
                    else {
                        correspondenceDtos.add(new CorrespondenceDto(
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
                    correspondenceDtos.add(new CorrespondenceDto(
                            chat.getId(),
                            chat.getName(),
                            message.getMessageText(),
                            message.getSendDate(),
                            message.getSenderId()
                    ));
                }
                else if (chat.getChatType().equals(ChatType.DIALOGUE)) {
                    if (authenticatedUserId.compareTo(chat.getReceiverId()) != 0) {
                        String fullName = integrationRequestsService
                                .getFullNameAndAvatarId(chat.getReceiverId()).get(0);
                        correspondenceDtos.add(new CorrespondenceDto(
                                chat.getId(),
                                fullName,
                                message.getMessageText(),
                                message.getSendDate(),
                                message.getSenderId()
                        ));
                    } else if (authenticatedUserId.compareTo(chat.getSenderId()) != 0) {
                        String fullName = integrationRequestsService
                                .getFullNameAndAvatarId(chat.getSenderId()).get(0);
                        correspondenceDtos.add(new CorrespondenceDto(
                                chat.getId(),
                                fullName,
                                message.getMessageText(),
                                message.getSendDate(),
                                message.getSenderId()
                        ));
                    }
                    else {
                        correspondenceDtos.add(new CorrespondenceDto(
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

        Comparator<CorrespondenceDto> sortByDate =
                Comparator.comparing(
                        dto -> dto.getLastMessageSendDate() != null ? dto.getLastMessageSendDate()
                                : LocalDateTime.MIN,
                        Comparator.reverseOrder()
                );
        correspondenceDtos.sort(sortByDate);

        CorrespondencesPageListDto result = new CorrespondencesPageListDto();
        result.setCorrespondences(correspondenceDtos);
        result.setPageInfo(new PageInfoDto(pageNumber, pageSize));
        result.setChatNameFilter(paginationWithChatNameDto.getChatName());

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
