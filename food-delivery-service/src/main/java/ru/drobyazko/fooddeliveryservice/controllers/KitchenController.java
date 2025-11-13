package ru.drobyazko.fooddeliveryservice.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.drobyazko.fooddeliveryservice.dtos.requests.CreateKitchenRequest;
import ru.drobyazko.fooddeliveryservice.dtos.KitchenDto;
import ru.drobyazko.fooddeliveryservice.dtos.responses.CreateKitchenResponse;
import ru.drobyazko.fooddeliveryservice.dtos.responses.GetKitchenResponse;
import ru.drobyazko.fooddeliveryservice.services.KitchenService;

import java.util.List;

//TODO: add validation and caching, return ResponseEntities(not sure yet)
@RestController
@RequestMapping("/kitchens")
public class KitchenController {
    private final KitchenService kitchenService;

    public KitchenController(KitchenService kitchenService) {
        this.kitchenService = kitchenService;
    }

    //TODO: continue working on KitchenIT and implementing this functionality according to tests
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long createKitchen(CreateKitchenRequest createKitchenRequest) {
        return 1L;
        //return kitchenService.createKitchen(createKitchenRequest);
    }


//    @PostMapping
//    public ResponseEntity<KitchenDto> createKitchen(CreateKitchenRequest createKitchenRequest) {
//        return new ResponseEntity<>(HttpStatus.CREATED);
//        return kitchenService.createKitchen(createKitchenRequest);
//    }

    //TODO: continue working on KitchenIT and implementing this functionality according to tests
    @GetMapping
    public GetKitchenResponse getKitchen(@RequestParam Long id) {
        return new GetKitchenResponse(1L, "a", "a");
    }

//    @GetMapping
//    public List<KitchenDto> getKitchens() {
//        return kitchenService.getKitchens();
//    }

    @PutMapping
    public void updateKitchen() {

    }

    @DeleteMapping
    public void deleteKitchen(Long id) {
        kitchenService.deleteKitchen(id);
    }
}
