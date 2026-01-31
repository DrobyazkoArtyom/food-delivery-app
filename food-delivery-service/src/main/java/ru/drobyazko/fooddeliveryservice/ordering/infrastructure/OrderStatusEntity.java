package ru.drobyazko.fooddeliveryservice.ordering.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_status")
public class OrderStatusEntity {
    @Id
    private Long id;
    @Column(nullable = false)
    private String orderStatus;

    protected OrderStatusEntity() {
    }

    public OrderStatusEntity(Long id, String orderStatus) {
        this.id = id;
        this.orderStatus = orderStatus;
    }

    public Long getId() {
        return id;
    }

    public String getOrderStatus() {
        return orderStatus;
    }
}
