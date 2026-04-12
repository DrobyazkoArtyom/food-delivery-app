package ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate;

public enum OrderStatus {
    CREATED("CREATED"),
    PREPARED("PREPARED");

    private final String status;

    OrderStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
