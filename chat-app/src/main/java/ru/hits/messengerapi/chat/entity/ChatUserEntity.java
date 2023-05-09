package ru.hits.messengerapi.chat.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

/**
 * Сущность чат-пользователь.
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "chat_user")
public class ChatUserEntity {

    /**
     * Идентификатор сущности чат-пользователь.
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    /**
     * Уникальный идентификатор чата.
     */
    @Column(name = "chat_id")
    private UUID chatId;

    /**
     * Уникальный идентификатор пользователя.
     */
    @Column(name = "user_id")
    private UUID userId;

}
