package ru.drobyazko.fooddeliveryservice.ordering.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusRecordEntity, Long> {
    List<OrderStatusRecordEntity> findByOrderIdOrderById(Long id);

    Optional<OrderStatusRecordEntity> findByOrderIdAndOrderStatus(Long orderId, String orderStatus);
}
