package ru.drobyazko.fooddeliveryservice.ordering.api;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.drobyazko.fooddeliveryservice.ordering.application.OrderService;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.Order;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.PlaceOrder;

@RestController
@RequestMapping(("/orders"))
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlaceOrderResponse placeOrder(@RequestBody @Valid PlaceOrderRequest placeOrderRequest) {
        PlaceOrder placeOrder = new PlaceOrder(1L, placeOrderRequest.menuItemStocks());
        Order order = orderService.placeOrder(placeOrder);
        return new PlaceOrderResponse(order.getId(), order.getOrderItems());
    }

    @GetMapping("/{id}")
    public GetOrderResponse getOrder(@PathVariable("id") Long id) {
        Order order = orderService.getOrder(id);
        return new GetOrderResponse(order.getId(), order.getOrderItems());
    }

    //TODO: publishOrderStatus
    // might be a rabbitmq listener?

//    @PostMapping
//    public Order createOrder(CreateOrderRequest createOrderRequest) {
//        return orderService.createOrder(createOrderRequest);
//    }

//    public void cancelOrder() {
//
//    }
}
