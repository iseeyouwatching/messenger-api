package ru.hits.messengerapi.chat.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private ChatEntity chat;

    @Column(name = "send_date")
    private LocalDateTime sendDate;

    @Column(name = "message_text", length = 500)
    private String messageText;

}
