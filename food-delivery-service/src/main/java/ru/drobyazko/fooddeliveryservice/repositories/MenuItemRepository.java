package ru.drobyazko.fooddeliveryservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.drobyazko.fooddeliveryservice.dtos.MenuItemDto;
import ru.drobyazko.fooddeliveryservice.entities.Kitchen;
import ru.drobyazko.fooddeliveryservice.entities.MenuItem;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByKitchen(Kitchen kitchen);
}
