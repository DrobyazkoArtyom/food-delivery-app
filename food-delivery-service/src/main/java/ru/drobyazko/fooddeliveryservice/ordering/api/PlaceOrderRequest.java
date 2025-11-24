package ru.drobyazko.fooddeliveryservice.ordering.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.MenuItemStock;

import java.util.Set;

public record PlaceOrderRequest(@Valid @NotNull Set<MenuItemStock> menuItemStocks) {
}
