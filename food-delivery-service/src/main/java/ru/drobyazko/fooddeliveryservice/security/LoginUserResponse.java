package ru.drobyazko.fooddeliveryservice.security;

import java.util.Collection;

public record LoginUserResponse(String username, Collection<?> grantedAuthorities) {
}
