package ru.drobyazko.fooddeliveryservice.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateKitchenRequest(@NotNull @NotBlank String name, @NotNull @NotBlank String address) {
}
