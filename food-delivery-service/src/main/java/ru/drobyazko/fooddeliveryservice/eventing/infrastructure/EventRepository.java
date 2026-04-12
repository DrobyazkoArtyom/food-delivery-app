package ru.drobyazko.fooddeliveryservice.eventing.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<EventEntity, Long> {
    Page<EventEntity> findAllByOrderById(Pageable pageable);
}
