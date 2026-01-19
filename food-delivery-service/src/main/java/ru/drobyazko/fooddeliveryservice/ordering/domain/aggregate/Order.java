package ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate;

import java.util.Set;

public class Order {
    private final Long id;
    private final Long userId;
    private final Set<OrderItem> orderItems;

    public Order(Long id, Long userId, Set<OrderItem> orderItems) {
        this.id = id;
        this.userId = userId;
        this.orderItems = orderItems;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Set<OrderItem> getOrderItems() {
        return orderItems;
    }
}
