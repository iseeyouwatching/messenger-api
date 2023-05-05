package ru.hits.messengerapi.common.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserSuccessfulLoginDto {

    private LocalDateTime dateTimeOfLogin;

    private String IP;

}
