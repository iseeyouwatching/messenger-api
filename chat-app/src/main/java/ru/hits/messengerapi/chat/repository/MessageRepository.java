package ru.hits.messengerapi.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hits.messengerapi.chat.entity.MessageEntity;

import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {
}
