package ru.drobyazko.fooddeliveryservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.*;
import ru.drobyazko.fooddeliveryservice.catalogue.api.CreateKitchenRequest;
import ru.drobyazko.fooddeliveryservice.catalogue.api.CreateKitchenResponse;
import ru.drobyazko.fooddeliveryservice.catalogue.api.CreateMenuItemRequest;
import ru.drobyazko.fooddeliveryservice.catalogue.api.CreateMenuItemResponse;
import ru.drobyazko.fooddeliveryservice.ordering.api.GetOrderResponse;
import ru.drobyazko.fooddeliveryservice.ordering.api.PlaceOrderRequest;
import ru.drobyazko.fooddeliveryservice.ordering.api.PlaceOrderResponse;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.MenuItemStock;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.OrderItem;

import java.math.BigDecimal;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(PostgreSQLContainerConfiguration.class)
class OrderIT {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenIHaveMenuItemStocks_WhenITryToPlaceAnOrder() {
        CreateKitchenResponse createKitchenResponse =
                KitchenApiHelper.createKitchen(webTestClient, new CreateKitchenRequest("test", "test"));
        CreateMenuItemResponse firstCreateMenuItemResponse = MenuItemApiHelper.createMenuItem(webTestClient,
                new CreateMenuItemRequest(createKitchenResponse.id(), "test1", "test1", new BigDecimal(1)));
        CreateMenuItemResponse secondCreateMenuItemResponse = MenuItemApiHelper.createMenuItem(webTestClient,
                new CreateMenuItemRequest(createKitchenResponse.id(), "test2", "test2", new BigDecimal(1)));

        Set<MenuItemStock> menuItemStocks = Set.of(
                new MenuItemStock(firstCreateMenuItemResponse.id(), 5),
                new MenuItemStock(secondCreateMenuItemResponse.id(), 10)
        );

        ResponseSpec placeOrderResponseSpec =
                OrderApiHelper.sendPlaceOrderRequest(webTestClient, new PlaceOrderRequest(menuItemStocks));
        placeOrderResponseSpec.expectStatus().isCreated();

        Set<OrderItem> orderItems = Set.of(
                new OrderItem(firstCreateMenuItemResponse.id(), "test1", "test1", new BigDecimal(1), 5),
                new OrderItem(secondCreateMenuItemResponse.id(), "test2", "test2", new BigDecimal(1), 10)
        );

        PlaceOrderResponse placeOrderResponse =
                OrderApiHelper.mapPlaceOrderResponse(placeOrderResponseSpec);
        itShouldReturnOrderWithSameMenuItems(placeOrderResponse, orderItems);
    }

    private void itShouldReturnOrderWithSameMenuItems(PlaceOrderResponse placeOrderResponse,
                                                      Set<OrderItem> orderItems) {
        Assertions.assertNotNull(placeOrderResponse.orderId());
        Assertions.assertTrue(orderItems.containsAll(placeOrderResponse.orderItems()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenIHavePlacedAnOrder_WhenITryToGetAnOrder() {
        CreateKitchenResponse createKitchenResponse =
                KitchenApiHelper.createKitchen(webTestClient, new CreateKitchenRequest("testName", "testAddress"));
        CreateMenuItemResponse firstCreateMenuItemResponse = MenuItemApiHelper.createMenuItem(webTestClient,
                new CreateMenuItemRequest(createKitchenResponse.id(), "test1", "test1", new BigDecimal(1)));
        CreateMenuItemResponse secondCreateMenuItemResponse = MenuItemApiHelper.createMenuItem(webTestClient,
                new CreateMenuItemRequest(createKitchenResponse.id(), "test2", "test2", new BigDecimal(1)));

        Set<MenuItemStock> menuItemStocks = Set.of(
                new MenuItemStock(firstCreateMenuItemResponse.id(), 5),
                new MenuItemStock(secondCreateMenuItemResponse.id(), 10)
        );

        ResponseSpec placeOrderResponseSpec =
                OrderApiHelper.sendPlaceOrderRequest(webTestClient, new PlaceOrderRequest(menuItemStocks));
        PlaceOrderResponse placeOrderResponse =
                OrderApiHelper.mapPlaceOrderResponse(placeOrderResponseSpec);

        Set<OrderItem> orderItems = Set.of(
                new OrderItem(firstCreateMenuItemResponse.id(), "test1", "test1", new BigDecimal(1), 5),
                new OrderItem(secondCreateMenuItemResponse.id(), "test2", "test2", new BigDecimal(1), 10)
        );

        ResponseSpec getOrderResponseSpec =
                OrderApiHelper.sendGetOrderRequest(webTestClient, placeOrderResponse.orderId());
        getOrderResponseSpec.expectStatus().isOk();

        GetOrderResponse getOrderResponse =
                OrderApiHelper.mapGetOrderResponse(getOrderResponseSpec);
        itShouldReturnOrderWithSameMenuItems(getOrderResponse, orderItems);
    }

    private void itShouldReturnOrderWithSameMenuItems(GetOrderResponse getOrderResponse,
                                                      Set<OrderItem> orderItems) {
        Assertions.assertNotNull(getOrderResponse.orderId());
        Assertions.assertTrue(orderItems.containsAll(getOrderResponse.orderItems()));
    }
}
