package ru.hits.messengerapi.chat.dto;

import lombok.*;

import java.util.UUID;

/**
 * DTO файла.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FileDto {

    /**
     * Идентификатор файла.
     */
    private UUID id;

    /**
     * Название файла.
     */
    private String name;

    /**
     * Размер файла в MB.
     */
    private String size;

}
