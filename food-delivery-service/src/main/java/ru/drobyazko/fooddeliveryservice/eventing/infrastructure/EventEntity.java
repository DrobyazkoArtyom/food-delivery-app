package ru.drobyazko.fooddeliveryservice.eventing.infrastructure;

import jakarta.persistence.*;

@Entity
@Table(name = "events")
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String type;
    @Column(nullable = false)
    private String payload;

    protected EventEntity() {
    }

    public EventEntity(String type, String payload) {
        this.type = type;
        this.payload = payload;
    }

    public EventEntity(Long id, String type, String payload) {
        this.id = id;
        this.type = type;
        this.payload = payload;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getPayload() {
        return payload;
    }
}
