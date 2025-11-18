package ru.drobyazko.fooddeliveryservice.catalogue.api;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.drobyazko.fooddeliveryservice.catalogue.application.MenuItemService;
import ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate.CreateMenuItem;
import ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate.MenuItem;

import java.util.List;

@RestController
@RequestMapping("/menuItems")
public class MenuItemController {
    private final MenuItemService menuItemService;

    @Autowired
    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CreateMenuItemResponse createMenuItem(@RequestBody @Valid CreateMenuItemRequest createMenuItemRequest) {
        CreateMenuItem createMenuItem =
                new CreateMenuItem(
                        createMenuItemRequest.kitchenId(),
                        createMenuItemRequest.name(),
                        createMenuItemRequest.description(),
                        createMenuItemRequest.price());
        MenuItem menuItem = menuItemService.createMenuItem(createMenuItem);
        return new CreateMenuItemResponse(
                menuItem.getId(),
                menuItem.getKitchenId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice());
    }

    @GetMapping("/{id}")
    GetMenuItemResponse getMenuItem(@PathVariable("id") Long id) {
        MenuItem menuItem = menuItemService.getMenuItem(id);
        return new GetMenuItemResponse(
                menuItem.getId(),
                menuItem.getKitchenId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice());
    }

    @GetMapping
    public List<GetMenuItemResponse> getMenu(@RequestParam("kitchenId") Long kitchenId) {
        List<MenuItem> menuItems = menuItemService.getKitchenMenu(kitchenId);
        return menuItems.stream()
                .map(menuItem ->
                        new GetMenuItemResponse(
                                menuItem.getId(),
                                menuItem.getKitchenId(),
                                menuItem.getName(),
                                menuItem.getDescription(),
                                menuItem.getPrice()))
                .toList();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMenuItem(@PathVariable("id") Long id) {
        menuItemService.deleteMenuItem(id);
    }

    //TODO: deleteMenuItem or an alternative approach would be just hiding them

//    @PostMapping
//    public MenuItemDto createMenuItem(CreateMenuItemRequest createMenuItemRequest) {
//        return menuItemService.createMenuItem(createMenuItemRequest);
//    }
//
//    @GetMapping
//    public List<MenuItemDto> getMenuItems(Long kitchenId) {
//        return menuItemService.getMenuItemsByKitchenId(kitchenId);
//    }
//
//    @PutMapping
//    public void updateMenuItem() {
//
//    }
//
//    // probably don't need this method, we need to just hide the menuitem
//    @DeleteMapping
//    public void deleteMenuItem(Long id) {
//        menuItemService.deleteMenuItem(id);
//    }
}
