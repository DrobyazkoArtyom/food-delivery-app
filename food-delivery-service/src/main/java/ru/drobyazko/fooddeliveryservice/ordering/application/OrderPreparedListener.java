package ru.drobyazko.fooddeliveryservice.ordering.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
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

    @KafkaListener(id = "orderFinalizer", topics = "orderPrepared")
    public void finalizeOrderKafka(OrderPreparedMessage orderPreparedMessage) {
        OrderStatusRecord orderStatusRecord = orderService.publishOrderStatus(orderPreparedMessage.orderId(), OrderStatus.PREPARED.getStatus());
        // TODO: instead of orElseThrow() we should send this message to dlt
        //  and we also do not really need to send userId over kafka. just find it using orderId
        UserEntity userEntity = userRepository.findById(orderPreparedMessage.userId()).orElseThrow();
        simpMessagingTemplate.convertAndSendToUser(userEntity.getUsername(), "/topic/order", new Object());
    }

}
