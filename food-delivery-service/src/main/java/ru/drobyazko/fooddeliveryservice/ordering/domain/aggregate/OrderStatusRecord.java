package ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate;

public record OrderStatusRecord(Long orderId, String orderStatus) {
}
