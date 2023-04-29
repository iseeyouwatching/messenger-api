package ru.hits.messengerapi.chat.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import ru.hits.messengerapi.chat.enumeration.ChatType;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

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

    @Column(name = "chat_type")
    private ChatType chatType;

    private String name;

    @Column(name = "admin_id")
    private UUID adminId;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Column(name = "avatar_id")
    private UUID avatarId;

}
