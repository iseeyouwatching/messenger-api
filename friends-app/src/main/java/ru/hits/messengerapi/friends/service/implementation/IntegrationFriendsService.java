package ru.hits.messengerapi.friends.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hits.messengerapi.common.exception.ConflictException;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.friends.entity.FriendEntity;
import ru.hits.messengerapi.friends.repository.FriendsRepository;

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
            throw new ConflictException("Пользователь с ID " + targetUserId + " не может написать пользователю с ID "
                    + addedUserId + ", так как они не являются друзьями.");
        }
        return true;
    }

}
