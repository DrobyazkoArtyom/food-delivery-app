package ru.drobyazko.fooddeliveryservice.security;

import java.util.Set;

public record RegisterUserResponse(Long id, String username, String password, Set<AuthorityType> authorityTypes) {
}
