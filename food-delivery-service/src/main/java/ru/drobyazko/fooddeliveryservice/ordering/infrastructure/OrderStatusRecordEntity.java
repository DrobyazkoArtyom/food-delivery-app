package ru.drobyazko.fooddeliveryservice.ordering.infrastructure;

import jakarta.persistence.*;

@Entity
@Table(name = "order_status_history")
public class OrderStatusRecordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long orderId;
    @Column(nullable = false)
    private Long orderStatusId;

    protected OrderStatusRecordEntity() {
    }

    public OrderStatusRecordEntity(Long orderId, Long orderStatusId) {
        this.orderId = orderId;
        this.orderStatusId = orderStatusId;
    }

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getOrderStatusId() {
        return orderStatusId;
    }
}
