package ru.hits.messengerapi.friends.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.hits.messengerapi.common.exception.ConflictException;
import ru.hits.messengerapi.common.security.JwtUserData;
import ru.hits.messengerapi.friends.dto.AddToFriendsDto;
import ru.hits.messengerapi.friends.dto.FriendDto;
import ru.hits.messengerapi.friends.entity.FriendEntity;
import ru.hits.messengerapi.friends.repository.FriendRepository;
import ru.hits.messengerapi.friends.service.FriendServiceInterface;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FriendService implements FriendServiceInterface {

    private final FriendRepository friendRepository;

    @Override
    public FriendDto addToFriends(AddToFriendsDto addToFriendsDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
        UUID targetUserId = userData.getId();

        if (friendRepository.findByTargetUserIdAndAddedUserId(
                targetUserId, addToFriendsDto.getAddedUserId()).isPresent()) {
            throw new ConflictException("Пользователь с ID " + addToFriendsDto.getAddedUserId() + " и ФИО "
                    + addToFriendsDto.getFriendName() + " уже добавлен в список друзей.");
        }

        FriendEntity friend = new FriendEntity();
        friend.setAddedDate(LocalDateTime.now());
        friend.setTargetUserId(targetUserId);
        friend.setAddedUserId(addToFriendsDto.getAddedUserId());
        friend.setFriendName(addToFriendsDto.getFriendName());

        friend = friendRepository.save(friend);

        return new FriendDto(friend);
    }
}
