package ru.drobyazko.fooddeliveryservice.ordering.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
}
