package ru.drobyazko.fooddeliveryservice.security.api;

import java.util.Collection;

public record LoginUserResponse(String username, Collection<?> grantedAuthorities) {
}
