package ru.hits.messengerapi.chat.dto;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AttachmentDto {

    private UUID fileId;

    private String fileName;

}
