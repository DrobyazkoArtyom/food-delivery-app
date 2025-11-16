package ru.drobyazko.fooddeliveryservice.catalogue.api;

import java.math.BigDecimal;

public record GetMenuItemResponse(Long id, Long kitchenId, String name, String description, BigDecimal price) {
}
