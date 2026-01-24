package ru.drobyazko.fooddeliveryservice.security.domain.aggregate;

import ru.drobyazko.fooddeliveryservice.security.infrastructure.Authority;

import java.util.Set;

public record RegisterUser(String username, String password, Set<Authority> authoritySet) {
}
