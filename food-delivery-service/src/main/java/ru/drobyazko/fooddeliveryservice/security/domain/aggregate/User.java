package ru.drobyazko.fooddeliveryservice.security.domain.aggregate;

import ru.drobyazko.fooddeliveryservice.security.infrastructure.Authority;

import java.util.Set;

public record User(long id, String username, String password, Set<Authority> authorities) {
}
