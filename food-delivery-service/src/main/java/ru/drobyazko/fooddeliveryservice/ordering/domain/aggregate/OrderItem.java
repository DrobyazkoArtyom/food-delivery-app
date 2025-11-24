package ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate;

import java.math.BigDecimal;

public record OrderItem(Long menuItemId, String name, String description, BigDecimal unitPrice, Integer quantity) {
}
