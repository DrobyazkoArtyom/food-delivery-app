package ru.drobyazko.fooddeliveryservice.ordering.infrastructure;

import jakarta.persistence.*;

@Entity
@Table(name = "customer_orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long userId;

    protected OrderEntity() {
    }

    public OrderEntity(Long userId) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }
}
