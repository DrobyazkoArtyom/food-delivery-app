package ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate;

import java.util.List;

public class CreateOrderRequest {
    private final Long userId;
    private final List<MenuItemIdQuantity> menuItemIdQuantities;

    public CreateOrderRequest(Long userId, List<MenuItemIdQuantity> menuItemIdQuantities) {
        this.userId = userId;
        this.menuItemIdQuantities = menuItemIdQuantities;
    }

    public Long getUserId() {
        return userId;
    }

    public List<MenuItemIdQuantity> getMenuItemIdQuantities() {
        return menuItemIdQuantities;
    }

}
