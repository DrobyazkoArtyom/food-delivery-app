package ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate;

import java.util.Set;

public record PlaceOrder(Long userId, Long kitchenId, Set<MenuItemStock> menuItemStocks) {
}
