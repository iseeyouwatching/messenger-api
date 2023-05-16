package ru.hits.messengerapi.filestorage.dto;

import lombok.*;

/**
 * DTO, представляющая данные для загрузки файла.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FileDownloadDto {

    /**
     * Байтовый массив содержащий содержимое файла.
     */
    private byte[] in;

    /**
     * Имя файла.
     */
    private String filename;

}
