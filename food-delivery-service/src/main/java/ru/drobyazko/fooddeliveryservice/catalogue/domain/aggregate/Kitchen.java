package ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate;

public class Kitchen {
    private final Long id;
    private final String name;
    private final String address;

    public Kitchen(Long id, String name, String address) {
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
