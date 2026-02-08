package ru.drobyazko.fooddeliveryservice.ordering.application;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate.MenuItem;
import ru.drobyazko.fooddeliveryservice.exceptions.PermissionDeniedException;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.*;
import ru.drobyazko.fooddeliveryservice.ordering.infrastructure.*;
import ru.drobyazko.fooddeliveryservice.catalogue.application.MenuItemService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final MenuItemService menuItemService;
    private final AmqpTemplate amqpTemplate;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        OrderStatusHistoryRepository orderStatusHistoryRepository,
                        MenuItemService menuItemService,
                        AmqpTemplate amqpTemplate) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
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

        OrderStatusRecord orderStatusRecord = saveOrderStatus(orderEntity.getId(), OrderStatus.CREATED.getId());

        amqpTemplate.convertAndSend("order.created." + placeOrder.kitchenId(),
                new OrderCreatedMessage(orderEntity.getId(), placeOrder.userId(), orderItems));
        return new Order(orderEntity.getId(), orderEntity.getUserId(), orderItems, List.of(orderStatusRecord));
    }

    //TODO: this should only be possible by user or a kitchen that relates to this order through menuItems
    // ^ (do kitchens even need to be able to do this operation?)
    @Transactional(readOnly = true)
    public Order getOrder(GetOrder getOrder) {
        OrderEntity orderEntity = orderRepository.findById(getOrder.id()).orElseThrow();
        if (!orderEntity.getUserId().equals(getOrder.userId())) {
            throw new PermissionDeniedException();
        }

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

        List<OrderStatusRecordEntity> orderStatusEntityHistory = orderStatusHistoryRepository.findByOrderIdOrderById(getOrder.id());
        List<OrderStatusRecord> orderStatusHistory = orderStatusEntityHistory.stream()
                .map(orderStatusRecordEntity -> new OrderStatusRecord(
                        orderStatusRecordEntity.getId(),
                        OrderStatus.valueFromId(orderStatusRecordEntity.getOrderStatusId()))
                )
                .toList();

        return new Order(orderEntity.getId(), orderEntity.getUserId(), orderItems, orderStatusHistory);
    }

    @Transactional
    public OrderStatusRecord saveOrderStatus(Long orderId, Long orderStatusId) {
        OrderStatusRecordEntity orderStatusRecordEntity =
                new OrderStatusRecordEntity(orderId, orderStatusId);
        orderStatusRecordEntity = orderStatusHistoryRepository.save(orderStatusRecordEntity);
        return new OrderStatusRecord(orderStatusRecordEntity.getId(),
                OrderStatus.valueFromId(orderStatusRecordEntity.getOrderStatusId()));
    }

}
