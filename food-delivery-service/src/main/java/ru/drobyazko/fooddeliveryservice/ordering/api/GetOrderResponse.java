package ru.drobyazko.fooddeliveryservice.ordering.api;

import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.OrderItem;

import java.util.Set;

public record GetOrderResponse(Long orderId, Set<OrderItem> orderItems) {
}
