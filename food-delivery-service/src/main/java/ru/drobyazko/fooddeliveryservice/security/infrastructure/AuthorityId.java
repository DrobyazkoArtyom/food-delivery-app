package ru.drobyazko.fooddeliveryservice.security.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AuthorityId implements Serializable {
    @Column(nullable = false, name = "user_id")
    private Long userId;
    @Column(nullable = false, name = "authority")
    private String authority;

    public AuthorityId() {
    }

    public AuthorityId(Long userId, String authority) {
        this.userId = userId;
        this.authority = authority;
    }

    public Long getUserId() {
        return userId;
    }

    public String getAuthority() {
        return authority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthorityId that)) return false;
        return Objects.equals(userId, that.userId) && Objects.equals(authority, that.authority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, authority);
    }
}
