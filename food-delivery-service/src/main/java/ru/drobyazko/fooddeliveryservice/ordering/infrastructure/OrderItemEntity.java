package ru.drobyazko.fooddeliveryservice.ordering.infrastructure;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_item")
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity orderEntity;
    @Column(nullable = false)
    private String name;
    private String description;
    @Column(nullable = false, columnDefinition = "NUMERIC")
    private BigDecimal unitPrice;
    @Column(nullable = false)
    private Integer quantity;

    protected OrderItemEntity() {
    }

    public OrderItemEntity(
            OrderEntity orderEntity,
            String name,
            String description,
            BigDecimal unitPrice,
            Integer quantity) {
        this.orderEntity = orderEntity;
        this.name = name;
        this.description = description;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public OrderEntity getOrderEntity() {
        return orderEntity;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
