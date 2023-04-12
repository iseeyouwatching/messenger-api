package ru.hits.messengerapi.common.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class JwtUserData {
    private final String login;

    private final UUID id;

    private final String fullName;

}
