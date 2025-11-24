package ru.drobyazko.fooddeliveryservice;

import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.*;
import ru.drobyazko.fooddeliveryservice.ordering.api.GetOrderResponse;
import ru.drobyazko.fooddeliveryservice.ordering.api.PlaceOrderRequest;
import ru.drobyazko.fooddeliveryservice.ordering.api.PlaceOrderResponse;

public class OrderApiHelper {
    public static ResponseSpec sendPlaceOrderRequest(WebTestClient webTestClient, PlaceOrderRequest placeOrderRequest) {
        return webTestClient.post()
                .uri("/orders")
                .bodyValue(placeOrderRequest)
                .exchange();
    }

    public static PlaceOrderResponse mapPlaceOrderResponse(ResponseSpec placeOrderResponseSpec) {
        return placeOrderResponseSpec.expectBody(PlaceOrderResponse.class)
                .returnResult()
                .getResponseBody();
    }

    public static ResponseSpec sendGetOrderRequest(WebTestClient webTestClient, Long id) {
        return webTestClient.get()
                .uri("/orders/" + id)
                .exchange();
    }

    public static GetOrderResponse mapGetOrderResponse(ResponseSpec getOrderResponseSpec) {
        return getOrderResponseSpec.expectBody(GetOrderResponse.class)
                .returnResult()
                .getResponseBody();
    }
}
