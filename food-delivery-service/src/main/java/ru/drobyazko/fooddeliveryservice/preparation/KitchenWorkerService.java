package ru.drobyazko.fooddeliveryservice.preparation;

import jakarta.transaction.Transactional;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.OrderCreatedMessage;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.OrderPreparedMessage;

// TODO: make this a separate application
//  This should be a separate app that allows kitchens to get orders, accept/decline them, see their pending orders and
//  complete them. Some operations are better be done locally (seeing pending orders in this application is going to be
//  bad for performance since it aggregates orders related to ALL kitchens in its database)
@Service
public class KitchenWorkerService {
    private final AmqpTemplate amqpTemplate;

    @Autowired
    public KitchenWorkerService(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    @RabbitListener(queues = "order-prepare-queue")
    @Transactional
    public void prepareOrder(OrderCreatedMessage orderCreatedMessage) throws InterruptedException {
        // simulate work
        Thread.sleep(3000L);
        amqpTemplate.convertAndSend("order.prepared",
                new OrderPreparedMessage(orderCreatedMessage.orderId(), orderCreatedMessage.userId()));
    }
}
