package ru.hits.messengerapi.filestorage.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FileDownloadDto {

    private byte[] in;

    private String filename;

}
