package ru.drobyazko.fooddeliveryservice.catalogue.api;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate.CreateKitchen;
import ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate.DeleteKitchen;
import ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate.Kitchen;
import ru.drobyazko.fooddeliveryservice.catalogue.application.KitchenService;
import ru.drobyazko.fooddeliveryservice.security.domain.aggregate.CustomUserDetails;

import java.util.List;

//TODO: add caching
@RestController
@RequestMapping("/kitchens")
public class KitchenController {
    private final KitchenService kitchenService;

    @Autowired
    public KitchenController(KitchenService kitchenService) {
        this.kitchenService = kitchenService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateKitchenResponse createKitchen(@RequestBody @Valid CreateKitchenRequest createKitchenRequest,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        CreateKitchen createKitchen =
                new CreateKitchen(userDetails.getId(), createKitchenRequest.name(), createKitchenRequest.address());
        Kitchen kitchen = kitchenService.createKitchen(createKitchen);
        return new CreateKitchenResponse(kitchen.id(), kitchen.name(), kitchen.address());
    }

    @GetMapping("/{id}")
    public GetKitchenResponse getKitchen(@PathVariable("id") Long id) {
        Kitchen kitchen = kitchenService.getKitchen(id);
        return new GetKitchenResponse(kitchen.id(), kitchen.name(), kitchen.address());
    }

    //TODO: should add paging
    @GetMapping
    public List<GetKitchenResponse> getAllKitchens() {
        List<Kitchen> kitchens = kitchenService.getAllKitchens();
        return kitchens.stream()
                .map(kitchen ->
                        new GetKitchenResponse(kitchen.id(), kitchen.name(), kitchen.address()))
                .toList();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteKitchen(@PathVariable("id") Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        DeleteKitchen deleteKitchen = new DeleteKitchen(id, userDetails.getId());
        kitchenService.deleteKitchen(deleteKitchen);
    }
}
