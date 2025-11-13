package ru.drobyazko.fooddeliveryservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drobyazko.fooddeliveryservice.dtos.requests.CreateKitchenRequest;
import ru.drobyazko.fooddeliveryservice.dtos.KitchenDto;
import ru.drobyazko.fooddeliveryservice.entities.Kitchen;
import ru.drobyazko.fooddeliveryservice.repositories.KitchenRepository;

import java.util.List;

@Service
public class KitchenService {
    private final KitchenRepository repository;

    @Autowired
    public KitchenService(KitchenRepository repository) {
        this.repository = repository;
    }

    public KitchenDto createKitchen(CreateKitchenRequest createKitchenRequest) {
        Kitchen kitchen = new Kitchen(createKitchenRequest.name(), createKitchenRequest.address());
        kitchen = repository.save(kitchen);
        return new KitchenDto(kitchen.getId(), kitchen.getName(), kitchen.getAddress());
    }

    public List<KitchenDto> getKitchens() {
        List<Kitchen> kitchens = repository.findAll();
        return kitchens
                .stream()
                .map(kitchen -> new KitchenDto(kitchen.getId(), kitchen.getName(), kitchen.getAddress()))
                .toList();
    }

    public void deleteKitchen(Long id) {
        repository.deleteById(id);
    }
}
