package ru.drobyazko.fooddeliveryservice.catalogue.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItemEntity, Long> {
    List<MenuItemEntity> findByKitchenEntity(KitchenEntity kitchenEntity);
}
