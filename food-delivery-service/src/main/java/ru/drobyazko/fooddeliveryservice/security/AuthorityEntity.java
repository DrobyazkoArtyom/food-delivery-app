package ru.drobyazko.fooddeliveryservice.security;

import jakarta.persistence.*;

@Entity
@Table(name = "authorities")
public class AuthorityEntity {
    @EmbeddedId
    private AuthorityId authorityId;

    protected AuthorityEntity() {
    }

    public AuthorityEntity(AuthorityId authorityId) {
        this.authorityId = authorityId;
    }

    public AuthorityId getAuthorityId() {
        return authorityId;
    }
}
