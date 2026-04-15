package ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateMenuItem(@NotNull Long kitchenId,
                             @NotBlank String name,
                             String description,
                             @NotNull @Positive BigDecimal price,
                             @NotNull Long userId) {
}
