package ru.drobyazko.fooddeliveryservice.dtos.requests;

public class AddToCartRequest {
    private final Long cartId;
    private final Long menuItemId;
    private final Integer quantity;

    public AddToCartRequest(Long cartId, Long menuItemId, Integer quantity) {
        this.cartId = cartId;
        this.menuItemId = menuItemId;
        this.quantity = quantity;
    }

    public Long getCartId() {
        return cartId;
    }

    public Long getMenuItemId() {
        return menuItemId;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
