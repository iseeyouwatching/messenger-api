package ru.hits.messengerapi.chat.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import ru.hits.messengerapi.chat.enumeration.ChatType;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Сущность чата.
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "chat")
public class ChatEntity {

    /**
     * Идентификатор сущности чата.
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    /**
     * Тип чата.
     */
    @Column(name = "chat_type")
    private ChatType chatType;

    /**
     * Название чата.
     */
    private String name;

    /**
     * Уникальный идентификатор администратора чата.
     */
    @Column(name = "admin_id")
    private UUID adminId;

    /**
     * Дата создания чата.
     */
    @Column(name = "creation_date")
    private LocalDate creationDate;

    /**
     * Уникальный идентификатор аватарки чата.
     */
    @Column(name = "avatar_id")
    private UUID avatarId;

    /**
     * Уникальный идентификатор отправителя.
     */
    @Column(name = "sender_id")
    private UUID senderId;

    /**
     * Уникальный идентификатор получателя.
     */
    @Column(name = "receiver_id")
    private UUID receiverId;

}
