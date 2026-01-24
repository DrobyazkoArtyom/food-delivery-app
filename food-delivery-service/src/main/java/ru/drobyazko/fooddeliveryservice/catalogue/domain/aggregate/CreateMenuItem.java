package ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate;

import java.math.BigDecimal;

public record CreateMenuItem(Long kitchenId, String name, String description, BigDecimal price, Long userId) {
}
