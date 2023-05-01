package ru.hits.messengerapi.chat.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CorrespondenceInfoDto {

    private String name;

    private UUID avatarId;

    private UUID adminId;

    private LocalDate creationDate;

}
