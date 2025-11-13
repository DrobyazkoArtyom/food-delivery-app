package ru.drobyazko.fooddeliveryservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.drobyazko.fooddeliveryservice.dtos.requests.CreateMenuItemRequest;
import ru.drobyazko.fooddeliveryservice.dtos.MenuItemDto;
import ru.drobyazko.fooddeliveryservice.services.MenuItemService;

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
    public MenuItemDto createMenuItem(CreateMenuItemRequest createMenuItemRequest) {
        return menuItemService.createMenuItem(createMenuItemRequest);
    }

    @GetMapping
    public List<MenuItemDto> getMenuItems(Long kitchenId) {
        return menuItemService.getMenuItemsByKitchenId(kitchenId);
    }

    @PutMapping
    public void updateMenuItem() {

    }

    // probably don't need this method, we need to just hide the menuitem
    @DeleteMapping
    public void deleteMenuItem(Long id) {
        menuItemService.deleteMenuItem(id);
    }
}
