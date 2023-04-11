package ru.hits.messengerapi.friends.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "friend")
public class FriendEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @Column(name = "added_date", nullable = false)
    private LocalDateTime addedDate;

    @Column(name = "deleted_date")
    private LocalDateTime deletedDate;

    @Column(name = "target_user_id", nullable = false)
    private UUID targetUserId;

    @Column(name = "added_user_id", nullable = false)
    private UUID addedUserId;

    @Column(name = "friend_name", nullable = false)
    private String friendName;

}