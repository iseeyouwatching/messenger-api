package ru.hits.messengerapi.common.security.props;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SecurityJwtTokenProps {

    private String[] permitAll;

    private String secret;

    private Long expiration;

    private String rootPath;

}
