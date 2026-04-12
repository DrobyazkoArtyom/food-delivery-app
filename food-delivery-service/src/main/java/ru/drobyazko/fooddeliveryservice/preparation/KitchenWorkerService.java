package ru.drobyazko.fooddeliveryservice.preparation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.OrderCreatedMessage;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.OrderPreparedMessage;

// TODO: make this a separate application
//  This should be a separate app that allows kitchens to get orders, accept/decline them, see their pending orders and
//  complete them. Some operations are better done locally (seeing pending orders in this application is going to be
//  bad for performance since it aggregates orders related to ALL kitchens in its database)
@Service
public class KitchenWorkerService {
    private final KafkaTemplate<String, OrderPreparedMessage> kafkaTemplate;

    @Autowired
    public KitchenWorkerService(@Qualifier("kafkaJsonTemplate") KafkaTemplate<String, OrderPreparedMessage> kafkaJsonTemplate) {
        this.kafkaTemplate = kafkaJsonTemplate;
    }

    @KafkaListener(id = "kitchenWorker", topics = "orderCreated")
    public void prepareOrderKafka(OrderCreatedMessage orderCreatedMessage) throws InterruptedException {
        // simulate work
        Thread.sleep(3000L);
        kafkaTemplate.send("orderPrepared", new OrderPreparedMessage(orderCreatedMessage.orderId(), orderCreatedMessage.userId()));
    }
}
