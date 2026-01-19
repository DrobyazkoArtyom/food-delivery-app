package ru.drobyazko.fooddeliveryservice.security;

import java.util.Set;

public record RegisterUser(String username, String password, Set<AuthorityType> authorityTypeSet) {
}
