package ru.drobyazko.fooddeliveryservice.dtos.requests;

public class SetQuantityCartItemRequest {
    private final Long cartItemId;
    private final Integer quantity;

    public SetQuantityCartItemRequest(Long cartItemId, Integer quantity) {
        this.cartItemId = cartItemId;
        this.quantity = quantity;
    }

    public Long getCartItemId() {
        return cartItemId;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
