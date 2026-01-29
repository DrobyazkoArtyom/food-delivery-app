package ru.drobyazko.fooddeliveryservice.catalogue.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate.CreateMenuItem;
import ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate.DeleteMenuItem;
import ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate.MenuItem;
import ru.drobyazko.fooddeliveryservice.catalogue.infrastructure.*;
import ru.drobyazko.fooddeliveryservice.exceptions.PermissionDeniedException;

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

    @Transactional
    public MenuItem createMenuItem(CreateMenuItem createMenuItem) {
        KitchenEntity kitchenEntity =
                kitchenRepository.findById(createMenuItem.kitchenId()).orElseThrow(KitchenNotFoundException::new);
        if (!kitchenEntity.getUserId().equals(createMenuItem.userId())) {
            throw new PermissionDeniedException();
        }

        MenuItemEntity menuItemEntity =
                new MenuItemEntity(
                        kitchenEntity,
                        createMenuItem.name(),
                        createMenuItem.description(),
                        createMenuItem.price());
        menuItemEntity = menuItemRepository.save(menuItemEntity);
        return new MenuItem(
                menuItemEntity.getId(),
                createMenuItem.kitchenId(),
                menuItemEntity.getName(),
                menuItemEntity.getDescription(),
                menuItemEntity.getPrice());
    }

    @Transactional(readOnly = true)
    public MenuItem getMenuItem(Long id) {
        MenuItemEntity menuItemEntity = menuItemRepository.findById(id).orElseThrow(MenuItemNotFoundException::new);
        return new MenuItem(
                menuItemEntity.getId(),
                menuItemEntity.getKitchenEntity().getId(),
                menuItemEntity.getName(),
                menuItemEntity.getDescription(),
                menuItemEntity.getPrice());
    }

    @Transactional(readOnly = true)
    public List<MenuItem> getKitchenMenu(Long kitchenId) {
        KitchenEntity kitchenEntity = kitchenRepository.getReferenceById(kitchenId);
        return menuItemRepository
                .findByKitchenEntity(kitchenEntity)
                .stream()
                .map(menuItemEntity ->
                        new MenuItem(
                                menuItemEntity.getId(),
                                kitchenId,
                                menuItemEntity.getName(),
                                menuItemEntity.getDescription(),
                                menuItemEntity.getPrice()))
                .toList();
    }

    //TODO: should probably implement a tombstone system instead of straight up deleting MenuItems from database
    @Transactional
    public void deleteMenuItem(DeleteMenuItem deleteMenuItem) {
        MenuItemEntity menuItemEntity =
                menuItemRepository.findById(deleteMenuItem.id()).orElseThrow(MenuItemNotFoundException::new);
        if (!menuItemEntity.getKitchenEntity().getUserId().equals(deleteMenuItem.userId())) {
            throw new PermissionDeniedException();
        }
        menuItemRepository.deleteById(deleteMenuItem.id());
    }
}
