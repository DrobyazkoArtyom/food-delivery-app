package ru.drobyazko.fooddeliveryservice.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "menu_item")
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Kitchen kitchen;
    @Column(nullable = false)
    private String name;
    @Column(nullable = true)
    private String description;
    @Column(nullable = false, columnDefinition = "NUMERIC")
    private BigDecimal price;
    @Column(nullable = false)
    private Boolean hidden = false;

    protected MenuItem() {
    }

    public MenuItem(Kitchen kitchen, String name, String description, BigDecimal price) {
        this.kitchen = kitchen;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public Kitchen getKitchen() {
        return kitchen;
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
