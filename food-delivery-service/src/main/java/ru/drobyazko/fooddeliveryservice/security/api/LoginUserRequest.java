package ru.drobyazko.fooddeliveryservice.security.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

//TODO: should think this through better, also think about imposing username/password constraints at database level
public record LoginUserRequest(@Valid @NotBlank String username,
                               @Valid @NotBlank String password) {
}
