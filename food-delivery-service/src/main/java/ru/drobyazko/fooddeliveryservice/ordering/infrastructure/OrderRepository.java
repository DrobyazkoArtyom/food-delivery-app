package ru.drobyazko.fooddeliveryservice.ordering.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
}
