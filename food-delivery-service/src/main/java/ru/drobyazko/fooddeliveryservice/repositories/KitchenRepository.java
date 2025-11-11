package ru.drobyazko.fooddeliveryservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.drobyazko.fooddeliveryservice.entities.Kitchen;

@Repository
public interface KitchenRepository extends JpaRepository<Kitchen, Long> {
}
