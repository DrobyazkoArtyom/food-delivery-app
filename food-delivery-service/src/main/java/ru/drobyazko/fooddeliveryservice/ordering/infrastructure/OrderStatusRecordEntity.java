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
    private String orderStatus;

    protected OrderStatusRecordEntity() {
    }

    public OrderStatusRecordEntity(Long orderId, String orderStatus) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
    }

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }
}
