package ru.drobyazko.fooddeliveryservice.security.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record LoginUserRequest(@Valid @NotBlank String username,
                               @Valid @NotBlank String password) {
}
