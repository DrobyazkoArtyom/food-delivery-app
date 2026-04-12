package ru.drobyazko.fooddeliveryservice.catalogue.api;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate.CreateKitchen;
import ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate.DeleteKitchen;
import ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate.Kitchen;
import ru.drobyazko.fooddeliveryservice.catalogue.application.KitchenService;
import ru.drobyazko.fooddeliveryservice.security.domain.aggregate.CustomUserDetails;

// TODO: add caching
//  while adding caching here is a good idea because this endpoints are gonna be hit very often
//  in a real project we should really think about if we actually need caching
//  having a good monitoring system could really help make an educated decision, so see next line
// TODO: research and try to connect this project to a monitoring system
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

    @GetMapping
    // TODO: need to finally add unit tests to this project, should test as much specific behaviours as possible
    //  for example if caching works as i expect in this method
    // TODO: how do we cache evict pageable's?
    @Cacheable("kitchens")
    public Page<GetKitchenResponse> getAllKitchens(Pageable pageable) {
        Page<Kitchen> kitchens = kitchenService.getAllKitchens(pageable);
        return kitchens.map(kitchen -> new GetKitchenResponse(kitchen.id(), kitchen.name(), kitchen.address()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(value = "myCache", allEntries = true)
    public void deleteKitchen(@PathVariable("id") Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        DeleteKitchen deleteKitchen = new DeleteKitchen(id, userDetails.getId());
        kitchenService.deleteKitchen(deleteKitchen);
    }
}
