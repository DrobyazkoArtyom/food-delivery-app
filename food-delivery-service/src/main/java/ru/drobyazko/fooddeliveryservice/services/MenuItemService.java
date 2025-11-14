package ru.drobyazko.fooddeliveryservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drobyazko.fooddeliveryservice.dtos.requests.CreateMenuItemRequest;
import ru.drobyazko.fooddeliveryservice.dtos.MenuItemDto;
import ru.drobyazko.fooddeliveryservice.entities.KitchenEntity;
import ru.drobyazko.fooddeliveryservice.entities.MenuItemEntity;
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
        MenuItemEntity menuItemEntity = menuItemRepository.findById(menuItemId).orElseThrow();
        return new MenuItemDto(
                menuItemEntity.getId(),
                menuItemEntity.getKitchenEntity().getId(),
                menuItemEntity.getName(),
                menuItemEntity.getDescription(),
                menuItemEntity.getPrice());
    }

    public MenuItemDto createMenuItem(CreateMenuItemRequest createMenuItemRequest) {
        KitchenEntity kitchenEntity = kitchenRepository.getReferenceById(createMenuItemRequest.getKitchenId());
        MenuItemEntity menuItemEntity = new MenuItemEntity(
                kitchenEntity,
                createMenuItemRequest.getName(),
                createMenuItemRequest.getDescription(),
                createMenuItemRequest.getPrice());
        menuItemEntity = menuItemRepository.save(menuItemEntity);
        return new MenuItemDto(
                menuItemEntity.getId(),
                createMenuItemRequest.getKitchenId(),
                menuItemEntity.getName(),
                menuItemEntity.getDescription(),
                menuItemEntity.getPrice());
    }

    public List<MenuItemDto> getMenuItemsByKitchenId(Long kitchenId) {
        KitchenEntity kitchenEntity = kitchenRepository.getReferenceById(kitchenId);
        return menuItemRepository
                .findByKitchenEntity(kitchenEntity)
                .stream()
                .map(menuItemEntity ->
                        new MenuItemDto(
                                menuItemEntity.getId(),
                                kitchenId,
                                menuItemEntity.getName(),
                                menuItemEntity.getDescription(),
                                menuItemEntity.getPrice()))
                .toList();
    }

    public void deleteMenuItem(Long id) {
        menuItemRepository.deleteById(id);
    }

}
