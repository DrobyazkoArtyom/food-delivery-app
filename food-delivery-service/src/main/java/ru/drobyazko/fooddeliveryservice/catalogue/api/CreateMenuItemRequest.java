package ru.drobyazko.fooddeliveryservice.catalogue.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateMenuItemRequest(@NotNull Long kitchenId,
                                    @NotBlank String name,
                                    String description,
                                    @Positive BigDecimal price) {
}
