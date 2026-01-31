package ru.drobyazko.fooddeliveryservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.drobyazko.fooddeliveryservice.catalogue.api.CreateKitchenRequest;
import ru.drobyazko.fooddeliveryservice.catalogue.api.CreateKitchenResponse;
import ru.drobyazko.fooddeliveryservice.catalogue.api.CreateMenuItemRequest;
import ru.drobyazko.fooddeliveryservice.catalogue.api.CreateMenuItemResponse;
import ru.drobyazko.fooddeliveryservice.ordering.api.GetOrderResponse;
import ru.drobyazko.fooddeliveryservice.ordering.api.PlaceOrderRequest;
import ru.drobyazko.fooddeliveryservice.ordering.api.PlaceOrderResponse;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.MenuItemStock;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.OrderItem;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.OrderStatus;

import java.math.BigDecimal;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import({PostgreSQLContainerConfiguration.class, RabbitMQContainerConfiguration.class, MockMvcConfiguration.class})
class OrderIT {
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setupUsers() throws Exception {
        TestUserHelpers.RegisterDefaultUsers(mockMvc);
    }

    @Test
    @Sql(scripts = "classpath:/TruncateAllTables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void givenIHaveMenuItemStocks_WhenITryToPlaceAnOrder() throws Exception {
        CreateKitchenResponse createKitchenResponse =
                KitchenApiHelper.createKitchen(mockMvc, new CreateKitchenRequest("test", "test"));
        CreateMenuItemResponse firstCreateMenuItemResponse = MenuItemApiHelper.createMenuItem(mockMvc,
                new CreateMenuItemRequest(createKitchenResponse.id(), "test1", "test1", new BigDecimal(1)));
        CreateMenuItemResponse secondCreateMenuItemResponse = MenuItemApiHelper.createMenuItem(mockMvc,
                new CreateMenuItemRequest(createKitchenResponse.id(), "test2", "test2", new BigDecimal(1)));

        Set<MenuItemStock> menuItemStocks = Set.of(
                new MenuItemStock(firstCreateMenuItemResponse.id(), 5),
                new MenuItemStock(secondCreateMenuItemResponse.id(), 10)
        );

        ResultActions placeOrderResultActions =
                OrderApiHelper.sendPlaceOrderRequest(mockMvc, new PlaceOrderRequest(createKitchenResponse.id(), menuItemStocks));
        placeOrderResultActions.andExpect(MockMvcResultMatchers.status().isCreated());

        Set<OrderItem> orderItems = Set.of(
                new OrderItem(firstCreateMenuItemResponse.id(), "test1", "test1", new BigDecimal(1), 5),
                new OrderItem(secondCreateMenuItemResponse.id(), "test2", "test2", new BigDecimal(1), 10)
        );

        PlaceOrderResponse placeOrderResponse =
                OrderApiHelper.mapPlaceOrderResponse(placeOrderResultActions);
        itShouldReturnOrderWithSameMenuItems(placeOrderResponse, orderItems);
    }

    private void itShouldReturnOrderWithSameMenuItems(PlaceOrderResponse placeOrderResponse,
                                                      Set<OrderItem> orderItems) {
        Assertions.assertNotNull(placeOrderResponse.orderId());
        Assertions.assertTrue(orderItems.containsAll(placeOrderResponse.orderItems()));
    }

    @Test
    @Sql(scripts = "classpath:/TruncateAllTables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void givenIHavePlacedAnOrder_WhenITryToGetAnOrder() throws Exception {
        CreateKitchenResponse createKitchenResponse =
                KitchenApiHelper.createKitchen(mockMvc, new CreateKitchenRequest("testName", "testAddress"));
        CreateMenuItemResponse firstCreateMenuItemResponse = MenuItemApiHelper.createMenuItem(mockMvc,
                new CreateMenuItemRequest(createKitchenResponse.id(), "test1", "test1", new BigDecimal(1)));
        CreateMenuItemResponse secondCreateMenuItemResponse = MenuItemApiHelper.createMenuItem(mockMvc,
                new CreateMenuItemRequest(createKitchenResponse.id(), "test2", "test2", new BigDecimal(1)));

        Set<MenuItemStock> menuItemStocks = Set.of(
                new MenuItemStock(firstCreateMenuItemResponse.id(), 5),
                new MenuItemStock(secondCreateMenuItemResponse.id(), 10)
        );

        ResultActions placeOrderResultActions =
                OrderApiHelper.sendPlaceOrderRequest(mockMvc, new PlaceOrderRequest(createKitchenResponse.id(), menuItemStocks));
        PlaceOrderResponse placeOrderResponse =
                OrderApiHelper.mapPlaceOrderResponse(placeOrderResultActions);

        Set<OrderItem> orderItems = Set.of(
                new OrderItem(firstCreateMenuItemResponse.id(), "test1", "test1", new BigDecimal(1), 5),
                new OrderItem(secondCreateMenuItemResponse.id(), "test2", "test2", new BigDecimal(1), 10)
        );

        ResultActions getOrderResultActions =
                OrderApiHelper.sendGetOrderRequest(mockMvc, placeOrderResponse.orderId());
        getOrderResultActions.andExpect(MockMvcResultMatchers.status().isOk());

        GetOrderResponse getOrderResponse =
                OrderApiHelper.mapGetOrderResponse(getOrderResultActions);
        itShouldReturnOrderWithSameMenuItems(getOrderResponse, orderItems);

        // TODO: while this works there are better ways. We can try using Awaitility (3rd party lib for waiting on async ops)
        // or we change this when we add real time notifications, then we can try to obtain a ws connection here
        Thread.sleep(5_000L);
        itShouldReturnCreatedAndPreparedOrderStatuses(getOrderResponse);
    }

    private void itShouldReturnOrderWithSameMenuItems(GetOrderResponse getOrderResponse,
                                                      Set<OrderItem> orderItems) {
        Assertions.assertNotNull(getOrderResponse.orderId());
        Assertions.assertTrue(orderItems.containsAll(getOrderResponse.orderItems()));
    }

    private void itShouldReturnCreatedAndPreparedOrderStatuses(GetOrderResponse getOrderResponse) {
        Assertions.assertEquals(OrderStatus.CREATED, getOrderResponse.orderStatusRecordList().get(0).orderStatus());
        Assertions.assertEquals(OrderStatus.PREPARED, getOrderResponse.orderStatusRecordList().get(1).orderStatus());
    }
}
