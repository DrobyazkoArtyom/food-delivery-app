package ru.drobyazko.fooddeliveryservice.dtos.requests;

public class CreateKitchenRequest {
    private final String name;
    private final String address;

    public CreateKitchenRequest(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}
