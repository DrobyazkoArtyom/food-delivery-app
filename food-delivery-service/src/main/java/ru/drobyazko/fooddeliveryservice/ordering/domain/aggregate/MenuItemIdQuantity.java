package ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate;

public class MenuItemIdQuantity {
    private final Long menuItemId;
    private final Integer quantity;

    public MenuItemIdQuantity(Long menuItemId, Integer quantity) {
        this.menuItemId = menuItemId;
        this.quantity = quantity;
    }

    public Long getMenuItemId() {
        return menuItemId;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
