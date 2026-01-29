package ru.drobyazko.fooddeliveryservice.ordering.application;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate.MenuItem;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.*;
import ru.drobyazko.fooddeliveryservice.ordering.infrastructure.*;
import ru.drobyazko.fooddeliveryservice.catalogue.application.MenuItemService;

import java.util.HashSet;
import java.util.Set;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuItemService menuItemService;
    private final AmqpTemplate amqpTemplate;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, MenuItemService menuItemService, AmqpTemplate amqpTemplate) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.menuItemService = menuItemService;
        this.amqpTemplate = amqpTemplate;
    }

    @Transactional
    public Order placeOrder(PlaceOrder placeOrder) {
        OrderEntity orderEntity = new OrderEntity(placeOrder.userId(), placeOrder.kitchenId());
        orderEntity = orderRepository.save(orderEntity);

        Set<OrderItem> orderItems = new HashSet<>();
        for (MenuItemStock menuItemStock : placeOrder.menuItemStocks()) {
            // TODO: a good optimization especially in the scenario where this is a call to a separate service somewhere on the network
            // ^ is batching all the menuitemids and getting them all in one call
            MenuItem menuItem = menuItemService.getMenuItem(menuItemStock.menuItemId());
            if (!menuItem.getKitchenId().equals(placeOrder.kitchenId())) {
                throw new InvalidOrderException();
            }
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
        // TODO: at this point we should hand off placed order to kitchens
        // maybe we post a message to mq or let mq read the order table (research transactional outbox pattern)
        // then kitchen side application gets their order from mq and posts the response back to mq
        // then user side application (this) can read order status when its updated
        amqpTemplate.convertAndSend("order.created", orderEntity.getId());
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
