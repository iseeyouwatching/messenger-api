package ru.hits.messengerapi.common.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class IntegrationAuthentication extends AbstractAuthenticationToken {

    public IntegrationAuthentication() {
        super(null);
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
