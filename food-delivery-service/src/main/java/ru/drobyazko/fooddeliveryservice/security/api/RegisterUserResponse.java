package ru.drobyazko.fooddeliveryservice.security.api;

import ru.drobyazko.fooddeliveryservice.security.domain.aggregate.Authority;

import java.util.Set;

public record RegisterUserResponse(Long id, String username, String password, Set<Authority> authorities) {
}
