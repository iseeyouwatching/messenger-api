package ru.hits.messengerapi.friends.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hits.messengerapi.friends.entity.BlacklistEntity;
import ru.hits.messengerapi.friends.entity.FriendEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BlacklistRepository extends JpaRepository<BlacklistEntity, UUID> {

    Optional<BlacklistEntity> findByTargetUserIdAndBlockedUserId(UUID targetUserId, UUID blockedUserId);

    List<BlacklistEntity> findAllByTargetUserIdAndDeletedDate(UUID targetUserId,
                                                           LocalDateTime deletedDate,
                                                           Pageable pageable);

    List<BlacklistEntity> findAllByTargetUserIdAndBlockedUserNameLikeAndDeletedDate(UUID targetUserId,
                                                                            String wildcardFullNameFilter,
                                                                            LocalDateTime deletedDate,
                                                                            Pageable pageable);

    List<BlacklistEntity> findAllByBlockedUserId(UUID blockedUserId);

}
