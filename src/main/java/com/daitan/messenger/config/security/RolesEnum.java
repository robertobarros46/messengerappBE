package com.daitan.messenger.config.security;

public enum RolesEnum {

    ADMIN("ADMIN"),
    USER("USER"),
    AUDITOR("AUDITOR");

    private String role;

    RolesEnum(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

}
