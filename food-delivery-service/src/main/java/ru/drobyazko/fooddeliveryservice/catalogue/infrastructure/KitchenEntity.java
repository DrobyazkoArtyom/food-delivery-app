package ru.drobyazko.fooddeliveryservice.catalogue.infrastructure;

import jakarta.persistence.*;

@Entity
@Table(name = "kitchen")
public class KitchenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String address;

    protected KitchenEntity() {
    }

    public KitchenEntity(String name, String address) {
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
