package ru.hits.messengerapi.chat.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO для представления информации о переписке, когда заходишь в нее.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CorrespondenceInfoDto {

    /**
     * Название переписки.
     */
    private String name;

    /**
     * Идентификатор аватара чата.
     */
    private UUID avatarId;

    /**
     * Идентификатор администратора чата.
     */
    private UUID adminId;

    /**
     * Дата создания переписки.
     */
    private LocalDate creationDate;

}
