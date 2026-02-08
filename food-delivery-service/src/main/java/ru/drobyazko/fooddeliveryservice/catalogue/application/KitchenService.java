package ru.drobyazko.fooddeliveryservice.catalogue.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate.CreateKitchen;
import ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate.DeleteKitchen;
import ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate.Kitchen;
import ru.drobyazko.fooddeliveryservice.catalogue.infrastructure.KitchenEntity;
import ru.drobyazko.fooddeliveryservice.catalogue.infrastructure.KitchenNotFoundException;
import ru.drobyazko.fooddeliveryservice.catalogue.infrastructure.KitchenRepository;
import ru.drobyazko.fooddeliveryservice.exceptions.PermissionDeniedException;

import java.util.List;

@Service
public class KitchenService {
    private final KitchenRepository repository;

    @Autowired
    public KitchenService(KitchenRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Kitchen createKitchen(CreateKitchen createKitchen) {
        KitchenEntity kitchenEntity = new KitchenEntity(createKitchen.userId(), createKitchen.name(), createKitchen.address());
        kitchenEntity = repository.save(kitchenEntity);
        return new Kitchen(kitchenEntity.getId(), kitchenEntity.getName(), kitchenEntity.getAddress());
    }

    @Transactional(readOnly = true)
    public Kitchen getKitchen(Long id) {
        KitchenEntity kitchenEntity = repository.findById(id).orElseThrow(KitchenNotFoundException::new);
        return new Kitchen(kitchenEntity.getId(), kitchenEntity.getName(), kitchenEntity.getAddress());
    }

    @Transactional(readOnly = true)
    public List<Kitchen> getAllKitchens() {
        List<KitchenEntity> kitchenEntities = repository.findAll();
        return kitchenEntities.stream()
                .map(kitchen -> new Kitchen(kitchen.getId(), kitchen.getName(), kitchen.getAddress()))
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<Kitchen> getAllKitchens(Pageable pageable) {
        Page<KitchenEntity> kitchenEntities = repository.findAll(pageable);
        return kitchenEntities.map(kitchen -> new Kitchen(kitchen.getId(), kitchen.getName(), kitchen.getAddress()));
    }

    @Transactional
    public void deleteKitchen(DeleteKitchen deleteKitchen) {
        KitchenEntity kitchenEntity = repository.findById(deleteKitchen.id()).orElseThrow(KitchenNotFoundException::new);
        if (!kitchenEntity.getUserId().equals(deleteKitchen.userId())) {
            throw new PermissionDeniedException();
        }
        repository.delete(kitchenEntity);
    }
}
