package ru.drobyazko.fooddeliveryservice.catalogue.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KitchenRepository extends JpaRepository<KitchenEntity, Long> {
}
