package ru.hits.messengerapi.friends.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hits.messengerapi.friends.entity.FriendEntity;

import java.util.UUID;

@Repository
public interface FriendRepository  extends JpaRepository<FriendEntity, UUID> {
}
