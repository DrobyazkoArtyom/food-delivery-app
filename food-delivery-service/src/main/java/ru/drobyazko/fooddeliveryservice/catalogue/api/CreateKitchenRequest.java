package ru.drobyazko.fooddeliveryservice.catalogue.api;

import jakarta.validation.constraints.NotBlank;

public record CreateKitchenRequest(@NotBlank String name, @NotBlank String address) {
}
