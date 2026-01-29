package ru.drobyazko.fooddeliveryservice.preparation;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class KitchenWorkerService {
    @RabbitListener
    public void prepareOrder() {

    }
}
