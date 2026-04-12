package ru.drobyazko.fooddeliveryservice.security.domain.aggregate;

import java.util.Set;

public record RegisterUser(String username, String password, Set<Authority> authoritySet) {
}
