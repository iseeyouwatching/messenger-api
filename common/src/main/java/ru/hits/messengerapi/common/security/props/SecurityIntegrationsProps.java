package ru.hits.messengerapi.common.security.props;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SecurityIntegrationsProps {
    private String apiKey;

    private String rootPath;

}
