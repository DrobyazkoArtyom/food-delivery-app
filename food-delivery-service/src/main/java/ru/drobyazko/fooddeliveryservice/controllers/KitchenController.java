package ru.drobyazko.fooddeliveryservice.controllers;

import org.springframework.web.bind.annotation.*;
import ru.drobyazko.fooddeliveryservice.dtos.requests.CreateKitchenRequest;
import ru.drobyazko.fooddeliveryservice.dtos.KitchenDto;
import ru.drobyazko.fooddeliveryservice.services.KitchenService;

import java.util.List;

//TODO: add validation and caching, return ResponseEntities(not sure yet)
@RestController("/kitchens")
public class KitchenController {
    private final KitchenService kitchenService;

    public KitchenController(KitchenService kitchenService) {
        this.kitchenService = kitchenService;
    }

    @PostMapping()
    public KitchenDto createKitchen(CreateKitchenRequest createKitchenRequest) {
        return kitchenService.createKitchen(createKitchenRequest);
    }

    @GetMapping
    public List<KitchenDto> getKitchens() {
        return kitchenService.getKitchens();
    }

    @PutMapping
    public void updateKitchen() {

    }

    @DeleteMapping
    public void deleteKitchen(Long id) {
        kitchenService.deleteKitchen(id);
    }
}
