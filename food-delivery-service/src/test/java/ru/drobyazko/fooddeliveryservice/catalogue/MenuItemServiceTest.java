package ru.drobyazko.fooddeliveryservice.catalogue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.drobyazko.fooddeliveryservice.catalogue.application.MenuItemService;
import ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate.CreateMenuItem;
import ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate.DeleteMenuItem;
import ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate.MenuItem;
import ru.drobyazko.fooddeliveryservice.catalogue.infrastructure.KitchenEntity;
import ru.drobyazko.fooddeliveryservice.catalogue.infrastructure.KitchenNotFoundException;
import ru.drobyazko.fooddeliveryservice.catalogue.infrastructure.KitchenRepository;
import ru.drobyazko.fooddeliveryservice.catalogue.infrastructure.MenuItemEntity;
import ru.drobyazko.fooddeliveryservice.catalogue.infrastructure.MenuItemNotFoundException;
import ru.drobyazko.fooddeliveryservice.catalogue.infrastructure.MenuItemRepository;
import ru.drobyazko.fooddeliveryservice.exceptions.PermissionDeniedException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuItemServiceTest {
    @Mock
    private MenuItemRepository menuItemRepository;
    @Mock
    private KitchenRepository kitchenRepository;
    @InjectMocks
    private MenuItemService menuItemService;

    @Test
    void givenValidData_WhenICreateMenuItem_ThenItIsCreatedSuccessfully() {
        KitchenEntity kitchenEntity = new KitchenEntity(1L, "kitchen", "address");
        CreateMenuItem createMenuItem = new CreateMenuItem(1L, "test", "test", BigDecimal.ONE, 1L);
        when(kitchenRepository.findById(1L)).thenReturn(Optional.of(kitchenEntity));
        when(menuItemRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MenuItem menuItem = menuItemService.createMenuItem(createMenuItem);
        Assertions.assertEquals(createMenuItem.kitchenId(), menuItem.getKitchenId());
        Assertions.assertEquals(createMenuItem.name(), menuItem.getName());
        Assertions.assertEquals(createMenuItem.description(), menuItem.getDescription());
        Assertions.assertEquals(createMenuItem.price(), menuItem.getPrice());

        verify(menuItemRepository).save(any());
    }

    @Test
    void givenMissingKitchen_WhenICreateMenuItem_ThenKitchenNotFoundExceptionIsThrown() {
        CreateMenuItem createMenuItem = new CreateMenuItem(1L, "test", "test", BigDecimal.ONE, 1L);
        when(kitchenRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(KitchenNotFoundException.class, () -> menuItemService.createMenuItem(createMenuItem));

        verify(menuItemRepository, never()).save(any());
    }

    @Test
    void givenKitchenOwnedByAnotherUser_WhenICreateMenuItem_ThenPermissionDeniedExceptionIsThrown() {
        KitchenEntity kitchenEntity = new KitchenEntity(1L, "kitchen", "address");
        CreateMenuItem createMenuItem = new CreateMenuItem(1L, "test", "test", BigDecimal.ONE, 2L);
        when(kitchenRepository.findById(1L)).thenReturn(Optional.of(kitchenEntity));

        Assertions.assertThrows(PermissionDeniedException.class, () -> menuItemService.createMenuItem(createMenuItem));

        verify(menuItemRepository, never()).save(any());
    }

    @Test
    void givenMissingMenuItem_WhenIGetMenuItem_ThenMenuItemNotFoundExceptionIsThrown() {
        when(menuItemRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(MenuItemNotFoundException.class, () -> menuItemService.getMenuItem(1L));
    }

    @Test
    void givenMissingKitchen_WhenIGetKitchenMenu_ThenKitchenNotFoundExceptionIsThrown() {
        when(kitchenRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(KitchenNotFoundException.class, () -> menuItemService.getKitchenMenu(1L));

        verify(menuItemRepository, never()).findByKitchenEntityAndIsDeletedFalseOrderById(any());
    }

    @Test
    void givenMissingMenuItem_WhenIMarkItAsDeleted_ThenMenuItemNotFoundExceptionIsThrown() {
        when(menuItemRepository.findById(1L)).thenReturn(Optional.empty());

        DeleteMenuItem deleteMenuItem = new DeleteMenuItem(1L, 1L);
        Assertions.assertThrows(MenuItemNotFoundException.class, () -> menuItemService.markMenuItemAsDeleted(deleteMenuItem));

        verify(menuItemRepository, never()).save(any());
    }

    @Test
    void givenOwnedMenuItem_WhenIMarkMenuItemAsDeleted_ThenItIsSoftDeletedAndSaved() {
        KitchenEntity kitchenEntity = new KitchenEntity(1L, "kitchen", "address");
        MenuItemEntity menuItemEntity = new MenuItemEntity(kitchenEntity, "test", "test", BigDecimal.ONE);
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(menuItemEntity));

        menuItemService.markMenuItemAsDeleted(new DeleteMenuItem(1L, 1L));

        Assertions.assertTrue(menuItemEntity.isDeleted());
        verify(menuItemRepository).save(menuItemEntity);
    }

    @Test
    void givenMenuItemOwnedByAnotherUser_WhenIMarkItAsDeleted_ThenPermissionDeniedExceptionIsThrown() {
        KitchenEntity kitchenEntity = new KitchenEntity(1L, "kitchen", "address");
        MenuItemEntity menuItemEntity = new MenuItemEntity(kitchenEntity, "test", "test", BigDecimal.ONE);
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(menuItemEntity));

        DeleteMenuItem deleteMenuItem = new DeleteMenuItem(1L, 2L);
        Assertions.assertThrows(PermissionDeniedException.class, () -> menuItemService.markMenuItemAsDeleted(deleteMenuItem));

        Assertions.assertFalse(menuItemEntity.isDeleted());
        verify(menuItemRepository, never()).save(any());
    }

}
