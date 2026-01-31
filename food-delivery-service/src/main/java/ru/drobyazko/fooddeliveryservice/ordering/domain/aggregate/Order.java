package ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate;

import java.util.List;
import java.util.Set;

public record Order(Long id, Long userId, Set<OrderItem> orderItems, List<OrderStatusRecord> orderStatusHistory) {
}
