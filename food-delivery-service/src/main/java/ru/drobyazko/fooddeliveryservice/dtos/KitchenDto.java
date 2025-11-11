package ru.drobyazko.fooddeliveryservice.dtos;

public class KitchenDto {
    private final Long id;
    private final String name;
    private final String address;

    public KitchenDto(Long id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}
