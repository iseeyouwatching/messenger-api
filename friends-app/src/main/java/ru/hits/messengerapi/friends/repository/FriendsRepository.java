package ru.hits.messengerapi.friends.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hits.messengerapi.friends.entity.FriendEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendsRepository extends JpaRepository<FriendEntity, UUID> {

    Optional<FriendEntity> findByTargetUserIdAndAddedUserId(UUID targetUserId, UUID addedUserId);

    List<FriendEntity> findAllByTargetUserIdAndDeletedDate(UUID targetUserId,
                                                           LocalDateTime deletedDate,
                                                           Pageable pageable);

    List<FriendEntity> findAllByTargetUserIdAndFriendNameLikeAndDeletedDate(UUID targetUserId,
                                                                            String wildcardFullNameFilter,
                                                                            LocalDateTime deletedDate,
                                                                            Pageable pageable);

    List<FriendEntity> findAllByAddedUserId(UUID addedUserId);

}
