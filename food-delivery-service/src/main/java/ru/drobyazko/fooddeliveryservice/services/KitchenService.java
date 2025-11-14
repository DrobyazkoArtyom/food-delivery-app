package ru.drobyazko.fooddeliveryservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drobyazko.fooddeliveryservice.dtos.CreateKitchen;
import ru.drobyazko.fooddeliveryservice.dtos.Kitchen;
import ru.drobyazko.fooddeliveryservice.entities.KitchenEntity;
import ru.drobyazko.fooddeliveryservice.exceptions.KitchenNotFoundException;
import ru.drobyazko.fooddeliveryservice.repositories.KitchenRepository;

import java.util.List;

@Service
public class KitchenService {
    private final KitchenRepository repository;

    @Autowired
    public KitchenService(KitchenRepository repository) {
        this.repository = repository;
    }

    public Kitchen createKitchen(CreateKitchen createKitchen) {
        KitchenEntity kitchenEntity = new KitchenEntity(createKitchen.name(), createKitchen.address());
        kitchenEntity = repository.save(kitchenEntity);
        return new Kitchen(kitchenEntity.getId(), kitchenEntity.getName(), kitchenEntity.getAddress());
    }

    public Kitchen getKitchen(Long id) {
        KitchenEntity kitchenEntity = repository.findById(id).orElseThrow(KitchenNotFoundException::new);
        return new Kitchen(kitchenEntity.getId(), kitchenEntity.getName(), kitchenEntity.getAddress());
    }

    public List<Kitchen> getAllKitchens() {
        List<KitchenEntity> kitchenEntities = repository.findAll();
        return kitchenEntities
                .stream()
                .map(kitchen -> new Kitchen(kitchen.getId(), kitchen.getName(), kitchen.getAddress()))
                .toList();
    }

    public void deleteKitchen(Long id) {
        repository.deleteById(id);
    }
}
