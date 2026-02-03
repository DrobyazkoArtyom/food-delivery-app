package ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate;

import java.util.Set;

// having username here is redundant but saves some calls to the database because of it is needed for ws messaging
public record PlaceOrder(Long userId, Long kitchenId, Set<MenuItemStock> menuItemStocks) {
}
