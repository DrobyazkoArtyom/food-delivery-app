package ru.drobyazko.fooddeliveryservice.dtos;

import java.util.List;

public class OrderDto {
    private final Long id;
    private final Long userId;
    private final List<OrderItemDto> orderItems;
}
