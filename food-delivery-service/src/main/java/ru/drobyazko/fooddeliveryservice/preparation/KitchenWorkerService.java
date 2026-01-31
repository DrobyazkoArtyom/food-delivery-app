package ru.drobyazko.fooddeliveryservice.preparation;

import jakarta.transaction.Transactional;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.OrderStatus;
import ru.drobyazko.fooddeliveryservice.ordering.infrastructure.OrderStatusHistoryRepository;
import ru.drobyazko.fooddeliveryservice.ordering.infrastructure.OrderStatusRecordEntity;

// TODO: make this a separate application
// This should be a separate app that allows kitchens to get orders, accept/decline them, see their pending orders and
// complete them. Some operations are better be done locally (seeing pending orders in this application is going to be
// bad for performance since it aggregates orders related to ALL kitchens in its database)
@Service
public class KitchenWorkerService {
    private OrderStatusHistoryRepository orderStatusHistoryRepository;
    private AmqpTemplate amqpTemplate;

    @Autowired
    public KitchenWorkerService(OrderStatusHistoryRepository orderStatusHistoryRepository, AmqpTemplate amqpTemplate) {
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.amqpTemplate = amqpTemplate;
    }

    @RabbitListener(queues = "order-prepare-queue")
    @Transactional
    public void prepareOrder(Long orderId) {
        OrderStatusRecordEntity orderStatusRecordEntity =
                new OrderStatusRecordEntity(orderId, OrderStatus.PREPARED.getId());
        orderStatusRecordEntity = orderStatusHistoryRepository.save(orderStatusRecordEntity);
        amqpTemplate.convertAndSend("order.prepared", orderId);
    }
}
