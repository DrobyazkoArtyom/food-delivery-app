package ru.drobyazko.fooddeliveryservice.ordering.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate.MenuItem;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.*;
import ru.drobyazko.fooddeliveryservice.ordering.infrastructure.OrderEntity;
import ru.drobyazko.fooddeliveryservice.ordering.infrastructure.OrderItemEntity;
import ru.drobyazko.fooddeliveryservice.ordering.infrastructure.OrderItemRepository;
import ru.drobyazko.fooddeliveryservice.ordering.infrastructure.OrderRepository;
import ru.drobyazko.fooddeliveryservice.catalogue.application.MenuItemService;

import java.util.HashSet;
import java.util.Set;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuItemService menuItemService;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, MenuItemService menuItemService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.menuItemService = menuItemService;
    }

    @Transactional
    public Order placeOrder(PlaceOrder order) {
        OrderEntity orderEntity = new OrderEntity(order.userId());
        orderEntity = orderRepository.save(orderEntity);

        Set<OrderItem> orderItems = new HashSet<>();
        for (MenuItemStock menuItemStock : order.menuItemStocks()) {
            //TODO: a good optimization especially in the scenario where this is a call to a separate service somewhere on the network
            // is batching all the menuitemids and getting them all in one call
            MenuItem menuItem = menuItemService.getMenuItem(menuItemStock.menuItemId());
            OrderItem orderItem = new OrderItem(
                    menuItem.getId(),
                    menuItem.getName(),
                    menuItem.getDescription(),
                    menuItem.getPrice(),
                    menuItemStock.quantity());
            orderItems.add(orderItem);
            OrderItemEntity orderItemEntity = new OrderItemEntity(
                    orderEntity,
                    menuItem.getId(),
                    menuItem.getName(),
                    menuItem.getDescription(),
                    menuItem.getPrice(),
                    menuItemStock.quantity());
            orderItemRepository.save(orderItemEntity);
        }
        return new Order(orderEntity.getId(), orderEntity.getUserId(), orderItems);
    }

    //TODO: this should only be possible by user or a kitchen that relates to this order through menuItems
    // ^ (do kitchens even need to be able to do this operation?)
    @Transactional(readOnly = true)
    public Order getOrder(GetOrder getOrder) {
        OrderEntity orderEntity = orderRepository.findByIdAndUserId(getOrder.id(), getOrder.userId()).orElseThrow();
        Set<OrderItem> orderItems = new HashSet<>();
        for (OrderItemEntity orderItemEntity : orderItemRepository.getOrderItemEntitiesByOrderEntity(orderEntity)) {
            OrderItem orderItem = new OrderItem(
                    orderItemEntity.getMenuItemId(),
                    orderItemEntity.getName(),
                    orderItemEntity.getDescription(),
                    orderItemEntity.getUnitPrice(),
                    orderItemEntity.getQuantity());
            orderItems.add(orderItem);
        }
        return new Order(orderEntity.getId(), orderEntity.getUserId(), orderItems);
    }

}
