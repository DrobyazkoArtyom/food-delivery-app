package ru.drobyazko.fooddeliveryservice.security;

import jakarta.persistence.*;

//TODO: not sure how close jpa mapping should match actual db schema. do we need a composite key expressed as embeddedId here?
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
