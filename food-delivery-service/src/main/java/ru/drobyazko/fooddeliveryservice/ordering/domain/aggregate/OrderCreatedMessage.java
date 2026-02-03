package ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate;

import java.util.Set;

public record OrderCreatedMessage(Long orderId, Long userId, Set<OrderItem> orderItems) {
}
