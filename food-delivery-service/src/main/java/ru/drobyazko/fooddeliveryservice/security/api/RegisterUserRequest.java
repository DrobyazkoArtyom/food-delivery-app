package ru.drobyazko.fooddeliveryservice.security.api;

import ru.drobyazko.fooddeliveryservice.security.infrastructure.Authority;

import java.util.Set;

public record RegisterUserRequest(String username, String password, Set<Authority> authorities) {
}
