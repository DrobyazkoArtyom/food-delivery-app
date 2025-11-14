package ru.drobyazko.fooddeliveryservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.drobyazko.fooddeliveryservice.entities.KitchenEntity;
import ru.drobyazko.fooddeliveryservice.entities.MenuItemEntity;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItemEntity, Long> {
    List<MenuItemEntity> findByKitchenEntity(KitchenEntity kitchenEntity);
}
