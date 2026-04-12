package ru.drobyazko.fooddeliveryservice.ordering.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.MenuItemStock;

import java.util.Set;

public record PlaceOrderRequest(@Valid @NotNull Long kitchenId,
                                @Valid @NotNull @NotEmpty Set<MenuItemStock> menuItemStocks) {
}
