package ru.drobyazko.fooddeliveryservice.catalogue.infrastructure;

import jakarta.persistence.*;

@Entity
@Table(name = "kitchens")
public class KitchenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String address;

    protected KitchenEntity() {
    }

    public KitchenEntity(Long userId, String name, String address) {
        this.userId = userId;
        this.name = name;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}
