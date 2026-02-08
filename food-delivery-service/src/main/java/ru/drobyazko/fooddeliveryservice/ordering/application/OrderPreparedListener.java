package ru.drobyazko.fooddeliveryservice.ordering.application;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.OrderPreparedMessage;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.OrderStatus;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.OrderStatusRecord;
import ru.drobyazko.fooddeliveryservice.security.infrastructure.UserEntity;
import ru.drobyazko.fooddeliveryservice.security.infrastructure.UserRepository;

@Component
public class OrderPreparedListener {
    private final OrderService orderService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserRepository userRepository;

    @Autowired
    public OrderPreparedListener(OrderService orderService,
                                 SimpMessagingTemplate simpMessagingTemplate,
                                 UserRepository userRepository) {
        this.orderService = orderService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.userRepository = userRepository;
    }

    @RabbitListener(queues = "order-finish-queue")
    public void finalizeOrder(OrderPreparedMessage orderPreparedMessage) {
        OrderStatusRecord orderStatusRecord =
                orderService.saveOrderStatus(orderPreparedMessage.orderId(), OrderStatus.PREPARED.getId());
        UserEntity userEntity = userRepository.findById(orderPreparedMessage.userId()).orElseThrow();
        simpMessagingTemplate.convertAndSendToUser(userEntity.getUsername(), "/topic/order", new Object());
    }

}
