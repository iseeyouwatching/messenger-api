package ru.hits.messengerapi.chat.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

/**
 * Сущность вложения.
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "attachment")
public class AttachmentEntity {

    /**
     * Идентификатор сущности вложения.
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    /**
     * Сообщение, к которому относится вложение.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private MessageEntity message;

    /**
     * Уникальный идентификатор файла в хранилище.
     */
    @Column(name = "file_id")
    private UUID fileId;

    /**
     * Название файла.
     */
    @Column(name = "file_name")
    private String fileName;

}
