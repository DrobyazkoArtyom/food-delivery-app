package ru.drobyazko.fooddeliveryservice.security;

public enum AuthorityType {
    USER("USER"),
    KITCHEN("KITCHEN"),
    ADMIN("ADMIN");

    private final String authorityType;

    AuthorityType(String authorityType) {
        this.authorityType = authorityType;
    }

    public String getAuthorityType() {
        return authorityType;
    }
}
