package ru.drobyazko.fooddeliveryservice.ordering.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
}
