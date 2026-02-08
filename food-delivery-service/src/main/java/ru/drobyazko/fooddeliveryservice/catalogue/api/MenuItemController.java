package ru.drobyazko.fooddeliveryservice.catalogue.api;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.drobyazko.fooddeliveryservice.catalogue.application.MenuItemService;
import ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate.CreateMenuItem;
import ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate.DeleteMenuItem;
import ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate.MenuItem;
import ru.drobyazko.fooddeliveryservice.security.domain.aggregate.CustomUserDetails;

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
    CreateMenuItemResponse createMenuItem(@RequestBody @Valid CreateMenuItemRequest createMenuItemRequest,
                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        CreateMenuItem createMenuItem =
                new CreateMenuItem(
                        createMenuItemRequest.kitchenId(),
                        createMenuItemRequest.name(),
                        createMenuItemRequest.description(),
                        createMenuItemRequest.price(),
                        userDetails.getId());
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
    public void deleteMenuItem(@PathVariable("id") Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        DeleteMenuItem deleteMenuItem = new DeleteMenuItem(id, userDetails.getId());
        menuItemService.markMenuItemAsDeleted(deleteMenuItem);
    }
}
