package ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate;

public record OrderPreparedMessage(Long orderId, Long userId) {
}
