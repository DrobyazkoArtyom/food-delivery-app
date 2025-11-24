package ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MenuItemStock(@NotNull Long menuItemId, @Positive Integer quantity) {
}
