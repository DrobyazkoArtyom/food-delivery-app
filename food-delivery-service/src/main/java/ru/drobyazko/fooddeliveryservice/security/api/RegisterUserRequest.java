package ru.drobyazko.fooddeliveryservice.security.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import ru.drobyazko.fooddeliveryservice.security.infrastructure.Authority;

import java.util.Set;

//TODO: should think this through better, also think about imposing username/password constraints at database level
public record RegisterUserRequest(@Valid @NotBlank String username,
                                  @Valid @NotBlank String password,
                                  @Valid @NotEmpty Set<Authority> authorities) {
}
