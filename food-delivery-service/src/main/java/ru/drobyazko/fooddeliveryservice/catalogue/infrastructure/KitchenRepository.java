package ru.drobyazko.fooddeliveryservice.catalogue.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KitchenRepository extends JpaRepository<KitchenEntity, Long> {
    void deleteByIdAndUserId(Long id, Long userId);

    Optional<KitchenEntity> findByIdAndUserId(Long id, Long userId);
}
