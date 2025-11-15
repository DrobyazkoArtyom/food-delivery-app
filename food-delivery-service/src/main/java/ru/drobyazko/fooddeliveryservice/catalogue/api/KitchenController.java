package ru.drobyazko.fooddeliveryservice.catalogue.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate.CreateKitchen;
import ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate.Kitchen;
import ru.drobyazko.fooddeliveryservice.catalogue.application.KitchenService;

import java.util.List;

//TODO: add caching
@RestController
@RequestMapping("/kitchens")
public class KitchenController {
    private final KitchenService kitchenService;

    public KitchenController(KitchenService kitchenService) {
        this.kitchenService = kitchenService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateKitchenResponse createKitchen(@RequestBody @Valid CreateKitchenRequest createKitchenRequest) {
        CreateKitchen createKitchen = new CreateKitchen(createKitchenRequest.name(), createKitchenRequest.address());
        Kitchen kitchen = kitchenService.createKitchen(createKitchen);
        return new CreateKitchenResponse(kitchen.getId(), kitchen.getName(), kitchen.getAddress());
    }

    @GetMapping("/{id}")
    public GetKitchenResponse getKitchen(@PathVariable("id") Long id) {
        Kitchen kitchen = kitchenService.getKitchen(id);
        return new GetKitchenResponse(kitchen.getId(), kitchen.getName(), kitchen.getAddress());
    }

    @GetMapping
    public List<GetKitchenResponse> getAllKitchens() {
        List<Kitchen> kitchens = kitchenService.getAllKitchens();
        return kitchens.stream()
                .map(kitchen -> new GetKitchenResponse(kitchen.getId(), kitchen.getName(), kitchen.getAddress()))
                .toList();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteKitchen(@PathVariable("id") Long id) {
        kitchenService.deleteKitchen(id);
    }
}
