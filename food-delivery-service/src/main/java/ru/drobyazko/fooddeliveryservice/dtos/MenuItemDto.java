package ru.drobyazko.fooddeliveryservice.dtos;

import java.math.BigDecimal;

public class MenuItemDto {
    private final Long id;
    private final Long kitchenId;
    private final String name;
    private final String description;
    private final BigDecimal price;

    public MenuItemDto(Long id, Long kitchenId, String name, String description, BigDecimal price) {
        this.id = id;
        this.kitchenId = kitchenId;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Long getId() {
        return id;
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
