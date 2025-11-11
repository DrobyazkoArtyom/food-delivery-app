package ru.drobyazko.fooddeliveryservice.dtos.requests;

import java.math.BigDecimal;

public class CreateMenuItemRequest {
    private final Long kitchenId;
    private final String name;
    private final String description;
    private final BigDecimal price;

    public CreateMenuItemRequest(Long kitchenId, String name, String description, BigDecimal price) {
        this.kitchenId = kitchenId;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Long getKitchenId() {
        return kitchenId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
