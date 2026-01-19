package ru.drobyazko.fooddeliveryservice.security;

import java.util.Set;

public record RegisterUserRequest(String username, String password, Set<AuthorityType> authorityTypes) {
}
