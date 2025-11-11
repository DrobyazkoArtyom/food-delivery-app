package ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate;

import ru.drobyazko.fooddeliveryservice.enums.OrderStatus;

import java.util.Set;

public class Order {
    private final Long id;
    private final Long userId;
    private final Set<OrderItem> orderItems;
    private final OrderStatus status;

    public Order(Long id, Long userId, Set<OrderItem> orderItems, OrderStatus status) {
        this.id = id;
        this.userId = userId;
        this.orderItems = orderItems;
        this.status = status;
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

    public OrderStatus getStatus() {
        return status;
    }
}
