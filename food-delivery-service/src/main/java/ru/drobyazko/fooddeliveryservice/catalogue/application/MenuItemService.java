package ru.drobyazko.fooddeliveryservice.catalogue.application;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate.CreateMenuItem;
import ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate.DeleteMenuItem;
import ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate.MenuItem;
import ru.drobyazko.fooddeliveryservice.catalogue.infrastructure.*;
import ru.drobyazko.fooddeliveryservice.exceptions.PermissionDeniedException;

import java.util.List;

@Service
@Validated
public class MenuItemService {
    private final MenuItemRepository menuItemRepository;
    private final KitchenRepository kitchenRepository;

    @Autowired
    public MenuItemService(MenuItemRepository menuItemRepository, KitchenRepository kitchenRepository) {
        this.menuItemRepository = menuItemRepository;
        this.kitchenRepository = kitchenRepository;
    }

    @Transactional
    public MenuItem createMenuItem(@Valid CreateMenuItem createMenuItem) {
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
    public MenuItem getMenuItem(@NotNull Long id) {
        MenuItemEntity menuItemEntity =
                menuItemRepository.findByIdAndIsDeletedFalse(id).orElseThrow(MenuItemNotFoundException::new);
        return new MenuItem(
                menuItemEntity.getId(),
                menuItemEntity.getKitchenEntity().getId(),
                menuItemEntity.getName(),
                menuItemEntity.getDescription(),
                menuItemEntity.getPrice());
    }

    @Transactional(readOnly = true)
    public List<MenuItem> getKitchenMenu(@NotNull Long kitchenId) {
        KitchenEntity kitchenEntity =
                kitchenRepository.findById(kitchenId).orElseThrow(KitchenNotFoundException::new);
        return menuItemRepository
                .findByKitchenEntityAndIsDeletedFalseOrderById(kitchenEntity)
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

    @Transactional
    public void markMenuItemAsDeleted(@Valid DeleteMenuItem deleteMenuItem) {
        MenuItemEntity menuItemEntity =
                menuItemRepository.findById(deleteMenuItem.id()).orElseThrow(MenuItemNotFoundException::new);
        if (!menuItemEntity.getKitchenEntity().getUserId().equals(deleteMenuItem.userId())) {
            throw new PermissionDeniedException();
        }
        menuItemEntity.setDeleted(true);
        menuItemRepository.save(menuItemEntity);
    }
}
