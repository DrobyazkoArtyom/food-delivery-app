package ru.drobyazko.fooddeliveryservice.ordering.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate.MenuItem;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.CreateOrderRequest;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.MenuItemIdQuantity;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.Order;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.OrderItem;
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

    public Order createOrder(CreateOrderRequest order) {
        OrderEntity orderEntity = new OrderEntity(order.getUserId());
        orderEntity = orderRepository.save(orderEntity);

        Set<OrderItem> orderItems = new HashSet<>();
        for (MenuItemIdQuantity menuItemIdQuantity : order.getMenuItemIdQuantities()) {
            MenuItem menuItem = menuItemService.getMenuItem(menuItemIdQuantity.getMenuItemId());
            // this is for returning to user without reading database after saving
            OrderItem orderItem = new OrderItem(
                    menuItem.getName(),
                    menuItem.getDescription(),
                    menuItem.getPrice(),
                    menuItemIdQuantity.getQuantity());
            orderItems.add(orderItem);
            // this is for saving to database
            OrderItemEntity orderItemEntity = new OrderItemEntity(
                    orderEntity,
                    menuItem.getName(),
                    menuItem.getDescription(),
                    menuItem.getPrice(),
                    menuItemIdQuantity.getQuantity());
            orderItemRepository.save(orderItemEntity);
        }
        return new Order(orderEntity.getId(), orderEntity.getUserId(), orderItems, orderEntity.getStatus());
    }

    public Order getOrder(Long id) {
        OrderEntity orderEntity = orderRepository.findById(id).orElseThrow();
        Set<OrderItem> orderItems = new HashSet<>();
        for (OrderItemEntity orderItemEntity : orderEntity.getOrderItemEntities()) {
            OrderItem orderItem = new OrderItem(
                    orderItemEntity.getName(),
                    orderItemEntity.getDescription(),
                    orderItemEntity.getUnitPrice(),
                    orderItemEntity.getQuantity());
            orderItems.add(orderItem);
        }
        return new Order(orderEntity.getId(), orderEntity.getUserId(), orderItems, orderEntity.getStatus());
    }

}
