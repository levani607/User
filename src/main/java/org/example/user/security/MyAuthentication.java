package org.example.user.security;

import org.example.user.model.enums.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

public class MyAuthentication implements Authentication {

    private final String token;
    private final UserRole role;
    private final Long principal;
    private final String username;
    private boolean isAuthenticated;


    public MyAuthentication(String token,UserRole role, Long principal, String username,boolean isAuthenticated) {
        this.role = role;
        this.token = token;
        this.principal = principal;
        this.username = username;
        this.isAuthenticated = isAuthenticated;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.isAuthenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return this.username;
    }
}
