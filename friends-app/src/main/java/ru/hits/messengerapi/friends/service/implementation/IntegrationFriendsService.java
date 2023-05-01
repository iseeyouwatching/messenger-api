package ru.hits.messengerapi.friends.service.implementation;

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

@Service
@RequiredArgsConstructor
@Slf4j
public class IntegrationFriendsService {

    /**
     * Репозиторий для работы с сущностью {@link FriendEntity}.
     */
    private final FriendsRepository friendsRepository;

    public Boolean checkExistenceInFriends(UUID targetUserId, UUID addedUserId) {
        Optional<FriendEntity> friend =
                friendsRepository.findByTargetUserIdAndAddedUserId(targetUserId, addedUserId);
        if (friend.isEmpty() || friend.get().getDeletedDate() != null) {
            throw new ForbiddenException("Пользователь с ID " + targetUserId + " не может написать пользователю с ID "
                    + addedUserId + ", так как они не являются друзьями.");
        }
        return true;
    }

    public Boolean checkMultiExistenceInFriends(List<UUID> uuids) {
        List<FriendEntity> friends = friendsRepository.findAllByTargetUserId(uuids.get(0));
        List<UUID> friendsIDs = new ArrayList<>();
        for (FriendEntity friend: friends) {
            friendsIDs.add(friend.getAddedUserId());
        }
        List<String> result = new ArrayList<>();
        for (int i = 1; i < uuids.size(); i++) {
            if (!friendsIDs.contains(uuids.get(i))) {
                result.add("Пользователь с ID " + uuids.get(0) + " не может добавить в чат пользователя с ID "
                        + uuids.get(i) + ", так как они не являются друзьями.");
            }
        }
        if (!result.isEmpty()) {
            throw new MultiForbiddenException(result);
        }
        return true;
    }

}
