package ru.drobyazko.fooddeliveryservice.ordering.infrastructure;

import jakarta.persistence.*;
import ru.drobyazko.fooddeliveryservice.enums.OrderStatus;

import java.util.Set;

@Entity
@Table(name = "order")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private Long userId;
    @OneToMany
    @JoinColumn(nullable = false)
    private Set<OrderItemEntity> orderItemEntities;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    protected OrderEntity() {
    }

    public OrderEntity(Long userId) {
        this.userId = userId;
        this.status = OrderStatus.CREATED;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Set<OrderItemEntity> getOrderItemEntities() {
        return orderItemEntities;
    }

    public OrderStatus getStatus() {
        return status;
    }
}
