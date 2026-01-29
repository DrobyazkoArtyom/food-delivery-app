package ru.drobyazko.fooddeliveryservice.ordering.infrastructure;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private Long kitchenId;

    protected OrderEntity() {
    }

    public OrderEntity(Long userId, Long kitchenId) {
        this.userId = userId;
        this.kitchenId = kitchenId;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getKitchenId() {
        return kitchenId;
    }
}
