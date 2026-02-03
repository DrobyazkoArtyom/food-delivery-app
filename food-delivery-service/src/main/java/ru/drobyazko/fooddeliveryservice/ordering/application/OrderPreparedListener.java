package ru.drobyazko.fooddeliveryservice.ordering.application;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.OrderPreparedMessage;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.OrderStatus;
import ru.drobyazko.fooddeliveryservice.ordering.infrastructure.OrderStatusHistoryRepository;
import ru.drobyazko.fooddeliveryservice.ordering.infrastructure.OrderStatusRecordEntity;
import ru.drobyazko.fooddeliveryservice.security.infrastructure.UserEntity;
import ru.drobyazko.fooddeliveryservice.security.infrastructure.UserRepository;

@Component
public class OrderPreparedListener {
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserRepository userRepository;

    @Autowired
    public OrderPreparedListener(OrderStatusHistoryRepository orderStatusHistoryRepository,
                                 SimpMessagingTemplate simpMessagingTemplate,
                                 UserRepository userRepository) {
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.userRepository = userRepository;
    }

    @RabbitListener(queues = "order-finish-queue")
    @Transactional
    public void finalizeOrder(OrderPreparedMessage orderPreparedMessage) {
        OrderStatusRecordEntity orderStatusRecordEntity =
                new OrderStatusRecordEntity(orderPreparedMessage.orderId(), OrderStatus.PREPARED.getId());
        orderStatusRecordEntity = orderStatusHistoryRepository.save(orderStatusRecordEntity);
        //TODO: should we validate amqp messages?
        UserEntity userEntity = userRepository.findById(orderPreparedMessage.userId()).orElseThrow();
        // TODO: this should not be transactional
        simpMessagingTemplate.convertAndSendToUser(userEntity.getUsername(), "/topic/order", new Object());
    }
}
