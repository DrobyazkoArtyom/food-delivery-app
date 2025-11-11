package ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate;

import java.math.BigDecimal;

public class OrderItem {
    private final String name;
    private final String description;
    private final BigDecimal unitPrice;
    private final Integer quantity;

    public OrderItem(String name, String description, BigDecimal unitPrice, Integer quantity) {
        this.name = name;
        this.description = description;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
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
