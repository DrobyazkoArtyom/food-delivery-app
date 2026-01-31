package ru.drobyazko.fooddeliveryservice.ordering.api;

import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.OrderItem;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.OrderStatusRecord;

import java.util.List;
import java.util.Set;

public record GetOrderResponse(Long orderId, Set<OrderItem> orderItems, List<OrderStatusRecord> orderStatusRecordList) {
}
