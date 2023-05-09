package ru.hits.messengerapi.chat.dto;

import lombok.*;

import java.util.UUID;

/**
 * DTO, содержащая информацию о вложении.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AttachmentDto {

    /**
     * Уникальный идентификатор файла.
     */
    private UUID fileId;

    /**
     * Имя файла.
     */
    private String fileName;

}
