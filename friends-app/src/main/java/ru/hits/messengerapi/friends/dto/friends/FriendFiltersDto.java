package ru.hits.messengerapi.friends.dto.friends;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Класс, представляющий DTO, содержащую фильтры для поиска друщей.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FriendFiltersDto {

    /**
     * Дата добавления в друзья.
     */
    private LocalDate addedDate;

    /**
     * Идентификатор пользователя, которого добавили в друзья.
     */
    private UUID addedUserId;

    /**
     * ФИО друга.
     */
    private String friendName;

}
