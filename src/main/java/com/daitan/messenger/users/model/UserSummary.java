package com.daitan.messenger.users.model;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UserSummary {

    private String id;
    private String name;
    private String email;
    private Collection<? extends GrantedAuthority> authorities;

    public UserSummary(String id, String email, String name, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.authorities = authorities;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }
}
