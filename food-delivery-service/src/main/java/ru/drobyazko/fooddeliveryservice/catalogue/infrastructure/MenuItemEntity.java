package ru.drobyazko.fooddeliveryservice.catalogue.infrastructure;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "menu_item")
public class MenuItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kitchen_id", nullable = false)
    private KitchenEntity kitchenEntity;
    @Column(nullable = false)
    private String name;
    @Column(nullable = true)
    private String description;
    @Column(nullable = false, columnDefinition = "NUMERIC")
    private BigDecimal price;
    @Column(nullable = false)
    private Boolean hidden = false;

    protected MenuItemEntity() {
    }

    public MenuItemEntity(KitchenEntity kitchenEntity, String name, String description, BigDecimal price) {
        this.kitchenEntity = kitchenEntity;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public KitchenEntity getKitchenEntity() {
        return kitchenEntity;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Boolean getHidden() {
        return hidden;
    }
}
