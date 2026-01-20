package ru.drobyazko.fooddeliveryservice.security;

import org.springframework.security.core.GrantedAuthority;

public enum AuthorityType implements GrantedAuthority {
    USER("USER"),
    KITCHEN("KITCHEN"),
    ADMIN("ADMIN");

    private final String authorityType;

    AuthorityType(String authorityType) {
        this.authorityType = authorityType;
    }

    @Override
    public String getAuthority() {
        return authorityType;
    }
}
