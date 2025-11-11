package ru.drobyazko.fooddeliveryservice.dtos;

import java.util.List;

public class CartDto {
    private final Long id;
    private final Long userId;
    private final List<CartItemDto> cartItemDtos;

    public CartDto(Long id, Long userId, List<CartItemDto> cartItemDtos) {
        this.id = id;
        this.userId = userId;
        this.cartItemDtos = cartItemDtos;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public List<CartItemDto> getCartItemDtos() {
        return cartItemDtos;
    }
}
