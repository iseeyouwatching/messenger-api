package ru.hits.messengerapi.friends.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hits.messengerapi.common.exception.ForbiddenException;
import ru.hits.messengerapi.friends.entity.FriendEntity;
import ru.hits.messengerapi.common.exception.MultiForbiddenException;
import ru.hits.messengerapi.friends.repository.FriendsRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис с логикой интеграционных запросов, связанных с друзьями.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IntegrationFriendsService {

    /**
     * Репозиторий для работы с сущностью {@link FriendEntity}.
     */
    private final FriendsRepository friendsRepository;

    /**
     * Метод ля проверки существования пользователя в друзьях у другого пользователя.
     *
     * @param targetUserId идентификатор пользователя, у которого проверяем существование в друзьях
     *                     другого пользователя.
     * @param addedUserId идентификатор пользователя, которого проверяем.
     * @return true - если пользователь существует в друзьях у другого пользователя.
     */
    public Boolean checkExistenceInFriends(UUID targetUserId, UUID addedUserId) {
        Optional<FriendEntity> friend =
                friendsRepository.findByTargetUserIdAndAddedUserId(targetUserId, addedUserId);
        if (friend.isEmpty() || friend.get().getDeletedDate() != null) {
            log.warn("Пользователь с ID {} не может написать пользователю с ID {}, так как они не являются друзьями.",
                    targetUserId, addedUserId);
            throw new ForbiddenException("Пользователь с ID " + targetUserId + " не может написать пользователю с ID "
                    + addedUserId + ", так как они не являются друзьями.");
        }
        return true;
    }

    /**
     * Метод ля проверки существования пользователей в друзьях у другого пользователя.
     *
     * @param uuids список идентификаторов пользователей.
     * @return true - если пользователи существуют в друзьях у другого пользователя.
     */
    public Boolean checkMultiExistenceInFriends(List<UUID> uuids) {
        List<FriendEntity> friends = friendsRepository.findAllByTargetUserId(uuids.get(0));
        List<UUID> friendsIDs = new ArrayList<>();
        for (FriendEntity friend: friends) {
            friendsIDs.add(friend.getAddedUserId());
        }
        List<String> result = new ArrayList<>();
        for (int i = 1; i < uuids.size(); i++) {
            if (!friendsIDs.contains(uuids.get(i))
                    || (friendsIDs.contains(uuids.get(i))
                    && friendsRepository.findByTargetUserIdAndAddedUserId(uuids.get(0),
                    uuids.get(i)).get().getIsDeleted())) {
                result.add("Пользователь с ID " + uuids.get(0) + " не может добавить в чат пользователя с ID "
                        + uuids.get(i) + ", так как они не являются друзьями.");
            }
        }
        if (!result.isEmpty()) {
            log.error("Проверка нахождения пользователей в друзьях у другого пользователя. Ошибка: {}", result);
            throw new MultiForbiddenException(result);
        }
        return true;
    }

}
