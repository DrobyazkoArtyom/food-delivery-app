package ru.drobyazko.fooddeliveryservice.security.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import ru.drobyazko.fooddeliveryservice.security.infrastructure.Authority;

import java.util.Set;

public record RegisterUserRequest(@Valid @NotBlank String username,
                                  @Valid @NotBlank String password,
                                  @Valid @NotEmpty Set<Authority> authorities) {
}
