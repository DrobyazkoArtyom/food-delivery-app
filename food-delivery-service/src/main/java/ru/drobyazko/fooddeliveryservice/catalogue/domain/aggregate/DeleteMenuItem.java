package ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate;

import jakarta.validation.constraints.NotNull;

public record DeleteMenuItem(@NotNull Long id, @NotNull Long userId) {
}
