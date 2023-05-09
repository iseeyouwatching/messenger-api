package ru.hits.messengerapi.chat.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Сущность сообщения.
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "message")
public class MessageEntity {

    /**
     * Идентификатор сущности сообщения.
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    /**
     * Чат, к которому относится сообщение.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private ChatEntity chat;

    /**
     * Дата и время отправки сообщения.
     */
    @Column(name = "send_date")
    private LocalDateTime sendDate;

    /**
     * Текст сообщения.
     */
    @Column(name = "message_text", length = 500)
    private String messageText;

    /**
     * Уникальный идентификатор отправителя.
     */
    @Column(name = "sender_id")
    private UUID senderId;

    /**
     * ФИО отправителя.
     */
    @Column(name = "sender_name")
    private String senderName;

    /**
     * Уникальный идентификатор аватарки отправителя.
     */
    @Column(name = "sender_avatar_id")
    private UUID senderAvatarId;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttachmentEntity> attachments;

}
