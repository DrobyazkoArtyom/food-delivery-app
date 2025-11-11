package ru.drobyazko.fooddeliveryservice.ordering.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.drobyazko.fooddeliveryservice.ordering.application.OrderService;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.CreateOrderRequest;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.Order;

@RestController("/order")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public Order getOrder(Long id) {
        return orderService.getOrder(id);
    }

    @PostMapping
    public Order createOrder(CreateOrderRequest createOrderRequest) {
        return orderService.createOrder(createOrderRequest);
    }

    public void cancelOrder() {

    }
}
