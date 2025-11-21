package com.example.demo.api.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.api.model.FilialEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FilialDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final transient FilialEntity filial;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_FILIAL"));
    }

    @Override
    public String getPassword() {
        return filial.getSenhaHash();
    }

    @Override
    public String getUsername() {
        return filial.getLogin();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(filial.getAtivo());
    }
}
