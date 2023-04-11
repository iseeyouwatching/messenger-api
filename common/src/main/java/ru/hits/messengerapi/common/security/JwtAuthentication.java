package ru.hits.messengerapi.common.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class JwtAuthentication extends AbstractAuthenticationToken {

    public JwtAuthentication(JwtUserData jwtUserData) {
        super(null);
        this.setDetails(jwtUserData);
        setAuthenticated(true);
    }
    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return getDetails();
    }

}
