package ru.drobyazko.fooddeliveryservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drobyazko.fooddeliveryservice.dtos.requests.CreateMenuItemRequest;
import ru.drobyazko.fooddeliveryservice.dtos.MenuItemDto;
import ru.drobyazko.fooddeliveryservice.entities.Kitchen;
import ru.drobyazko.fooddeliveryservice.entities.MenuItem;
import ru.drobyazko.fooddeliveryservice.repositories.KitchenRepository;
import ru.drobyazko.fooddeliveryservice.repositories.MenuItemRepository;

import java.util.List;

@Service
public class MenuItemService {
    private final MenuItemRepository menuItemRepository;
    private final KitchenRepository kitchenRepository;

    @Autowired
    public MenuItemService(MenuItemRepository menuItemRepository, KitchenRepository kitchenRepository) {
        this.menuItemRepository = menuItemRepository;
        this.kitchenRepository = kitchenRepository;
    }

    public MenuItemDto getMenuItem(Long menuItemId) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId).orElseThrow();
        return new MenuItemDto(
                menuItem.getId(),
                menuItem.getKitchen().getId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice());
    }

    public MenuItemDto createMenuItem(CreateMenuItemRequest createMenuItemRequest) {
        Kitchen kitchen = kitchenRepository.getReferenceById(createMenuItemRequest.getKitchenId());
        MenuItem menuItem = new MenuItem(
                kitchen,
                createMenuItemRequest.getName(),
                createMenuItemRequest.getDescription(),
                createMenuItemRequest.getPrice());
        menuItem = menuItemRepository.save(menuItem);
        return new MenuItemDto(
                menuItem.getId(),
                createMenuItemRequest.getKitchenId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice());
    }

    public List<MenuItemDto> getMenuItemsByKitchenId(Long kitchenId) {
        Kitchen kitchen = kitchenRepository.getReferenceById(kitchenId);
        return menuItemRepository
                .findByKitchen(kitchen)
                .stream()
                .map(menuItem ->
                        new MenuItemDto(
                                menuItem.getId(),
                                kitchenId,
                                menuItem.getName(),
                                menuItem.getDescription(),
                                menuItem.getPrice()))
                .toList();
    }

    public void deleteMenuItem(Long id) {
        menuItemRepository.deleteById(id);
    }

}
