package ru.drobyazko.fooddeliveryservice.security;

import java.util.Set;

public record User(long id, String username, String password, Set<Authority> authorities) {
}
