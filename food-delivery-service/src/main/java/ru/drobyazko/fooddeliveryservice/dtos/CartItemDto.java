package ru.drobyazko.fooddeliveryservice.dtos;

public class CartItemDto {
    private final Long id;
    private final Long cartId;
    private final Long menuItemId;
    private final Integer quantity;

    public CartItemDto(Long id, Long cartId, Long menuItemId, Integer quantity) {
        this.id = id;
        this.cartId = cartId;
        this.menuItemId = menuItemId;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
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
