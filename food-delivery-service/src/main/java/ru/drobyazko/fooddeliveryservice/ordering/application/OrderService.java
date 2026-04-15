package ru.drobyazko.fooddeliveryservice.ordering.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.drobyazko.fooddeliveryservice.catalogue.domain.aggregate.MenuItem;
import ru.drobyazko.fooddeliveryservice.eventing.infrastructure.EventEntity;
import ru.drobyazko.fooddeliveryservice.eventing.infrastructure.EventRepository;
import ru.drobyazko.fooddeliveryservice.exceptions.PermissionDeniedException;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.*;
import ru.drobyazko.fooddeliveryservice.ordering.infrastructure.*;
import ru.drobyazko.fooddeliveryservice.catalogue.application.MenuItemService;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final EventRepository eventRepository;
    private final MenuItemService menuItemService;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        OrderStatusHistoryRepository orderStatusHistoryRepository,
                        EventRepository eventRepository,
                        MenuItemService menuItemService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.eventRepository = eventRepository;
        this.menuItemService = menuItemService;
    }

    @Transactional(rollbackFor = JsonProcessingException.class)
    public Order placeOrder(PlaceOrder placeOrder) throws JsonProcessingException {
        OrderEntity orderEntity = new OrderEntity(placeOrder.userId(), placeOrder.kitchenId());
        orderEntity = orderRepository.save(orderEntity);

        Set<OrderItem> orderItems = new HashSet<>();
        for (MenuItemStock menuItemStock : placeOrder.menuItemStocks()) {
            // TODO: a good optimization especially in the scenario where this is a call to a separate service somewhere on the network
            //  is batching all the menuitemids and getting them all in one call
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
        if (orderItems.isEmpty()) {
            throw new InvalidOrderException();
        }

        OrderStatusRecord orderStatusRecord = publishOrderStatus(orderEntity.getId(), OrderStatus.CREATED.getStatus());
        String payload = new ObjectMapper().writeValueAsString(
                new OrderCreatedMessage(orderEntity.getId(), placeOrder.userId(), orderItems));
        EventEntity eventEntity = new EventEntity("orderCreated", payload);
        eventRepository.save(eventEntity);
        return new Order(orderEntity.getId(), orderEntity.getUserId(), orderItems, List.of(orderStatusRecord));
    }

    //TODO: this should only be possible by user or a kitchen that relates to this order through menuItems
    // ^ (do kitchens even need to be able to do this operation?)
    @Transactional(readOnly = true)
    public Order getOrder(GetOrder getOrder) {
        OrderEntity orderEntity = orderRepository.findById(getOrder.id()).orElseThrow(OrderNotFoundException::new);
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
                        orderStatusRecordEntity.getOrderId(),
                        orderStatusRecordEntity.getOrderStatus())
                )
                .toList();

        return new Order(orderEntity.getId(), orderEntity.getUserId(), orderItems, orderStatusHistory);
    }

    @Transactional
    public OrderStatusRecord publishOrderStatus(Long orderId, String orderStatus) {
        // TODO: for kafka path we should also check if order even exists
        OrderStatusRecordEntity orderStatusRecordEntity =
                new OrderStatusRecordEntity(orderId, orderStatus);
        Optional<OrderStatusRecordEntity> statusHistory = orderStatusHistoryRepository.findByOrderIdAndOrderStatus(orderId, orderStatus);
        if (statusHistory.isEmpty()) {
            orderStatusHistoryRepository.save(orderStatusRecordEntity);
        }
        return new OrderStatusRecord(orderId, orderStatus);
    }

}
