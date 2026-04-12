package ru.drobyazko.fooddeliveryservice.security.domain.aggregate;

import org.springframework.security.core.GrantedAuthority;

public enum Authority implements GrantedAuthority {
    USER("USER"),
    KITCHEN("KITCHEN"),
    ADMIN("ADMIN");

    private final String role;

    Authority(String role) {
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return role;
    }
}
