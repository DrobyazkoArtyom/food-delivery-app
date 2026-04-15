package ru.drobyazko.fooddeliveryservice.catalogue.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuItemRepository extends JpaRepository<MenuItemEntity, Long> {
    List<MenuItemEntity> findByKitchenEntityAndIsDeletedFalseOrderById(KitchenEntity kitchenEntity);

    Optional<MenuItemEntity> findByIdAndIsDeletedFalse(Long id);
}
