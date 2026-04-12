package ru.drobyazko.fooddeliveryservice.ordering;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.drobyazko.fooddeliveryservice.security.TestUserHelpers;
import ru.drobyazko.fooddeliveryservice.catalogue.KitchenApiHelper;
import ru.drobyazko.fooddeliveryservice.catalogue.MenuItemApiHelper;
import ru.drobyazko.fooddeliveryservice.catalogue.api.CreateKitchenRequest;
import ru.drobyazko.fooddeliveryservice.catalogue.api.CreateKitchenResponse;
import ru.drobyazko.fooddeliveryservice.catalogue.api.CreateMenuItemRequest;
import ru.drobyazko.fooddeliveryservice.catalogue.api.CreateMenuItemResponse;
import ru.drobyazko.fooddeliveryservice.configuration.KafkaContainerConfiguration;
import ru.drobyazko.fooddeliveryservice.configuration.MockMvcConfiguration;
import ru.drobyazko.fooddeliveryservice.configuration.PostgreSQLContainerConfiguration;
import ru.drobyazko.fooddeliveryservice.ordering.api.GetOrderResponse;
import ru.drobyazko.fooddeliveryservice.ordering.api.PlaceOrderRequest;
import ru.drobyazko.fooddeliveryservice.ordering.api.PlaceOrderResponse;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.MenuItemStock;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.OrderItem;
import ru.drobyazko.fooddeliveryservice.ordering.domain.aggregate.OrderStatus;

import java.math.BigDecimal;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import({PostgreSQLContainerConfiguration.class, KafkaContainerConfiguration.class, MockMvcConfiguration.class})
class OrderIT {
    private static final Long DEFAULT_AWAIT_MESSAGE_TIMEOUT = 10L;
    private final MockMvc mockMvc;
    private final KafkaAdmin kafkaAdmin;
    @LocalServerPort
    private String port;

    @Autowired
    public OrderIT(MockMvc mockMvc, KafkaAdmin kafkaAdmin) {
        this.mockMvc = mockMvc;
        this.kafkaAdmin = kafkaAdmin;
    }

    @BeforeEach
    void setupUsers() throws Exception {
        TestUserHelpers.RegisterDefaultUsers(mockMvc);
    }

    @AfterEach
    void deleteKafkaTopics() {
        kafkaAdmin.deleteTopics("orderCreated", "orderPrepared");
    }

    @Test
    @Sql(scripts = "classpath:/TruncateAllTables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void givenIHaveMenuItemStocks_WhenITryToPlaceAnOrder() throws Exception {
        kafkaAdmin.createOrModifyTopics(TopicBuilder.name("orderCreated").partitions(1).replicas(1).build());
        kafkaAdmin.createOrModifyTopics(TopicBuilder.name("orderPrepared").partitions(1).replicas(1).build());

        Thread.sleep(5000L);

        CountDownLatch countDownLatchUntilMessageArrived = new CountDownLatch(1);
        StompSession stompSession = WebSocketHelper.connectToWebSocket(port);
        WebSocketHelper.subscribeToWebSocket(stompSession, "/user/topic/order", countDownLatchUntilMessageArrived::countDown);

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

        Assertions.assertTrue(countDownLatchUntilMessageArrived.await(DEFAULT_AWAIT_MESSAGE_TIMEOUT, TimeUnit.SECONDS));
    }

    private void itShouldReturnOrderWithSameMenuItems(PlaceOrderResponse placeOrderResponse,
                                                      Set<OrderItem> orderItems) {
        Assertions.assertNotNull(placeOrderResponse.orderId());
        Assertions.assertTrue(orderItems.containsAll(placeOrderResponse.orderItems()));
    }

    @Test
    @Sql(scripts = "classpath:/TruncateAllTables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void givenIHavePlacedAnOrder_WhenITryToGetAnOrder() throws Exception {
        CountDownLatch countDownLatchUntilMessageArrived = new CountDownLatch(1);
        StompSession stompSession = WebSocketHelper.connectToWebSocket(port);
        WebSocketHelper.subscribeToWebSocket(stompSession, "/user/topic/order", countDownLatchUntilMessageArrived::countDown);

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

        Assertions.assertTrue(countDownLatchUntilMessageArrived.await(DEFAULT_AWAIT_MESSAGE_TIMEOUT, TimeUnit.SECONDS));

        ResultActions getOrderResultActions =
                OrderApiHelper.sendGetOrderRequest(mockMvc, placeOrderResponse.orderId());
        getOrderResultActions.andExpect(MockMvcResultMatchers.status().isOk());

        GetOrderResponse getOrderResponse =
                OrderApiHelper.mapGetOrderResponse(getOrderResultActions);
        Set<OrderItem> orderItems = Set.of(
                new OrderItem(firstCreateMenuItemResponse.id(), "test1", "test1", new BigDecimal(1), 5),
                new OrderItem(secondCreateMenuItemResponse.id(), "test2", "test2", new BigDecimal(1), 10)
        );
        itShouldReturnOrderWithSameMenuItems(getOrderResponse, orderItems);
        itShouldReturnCreatedAndPreparedOrderStatuses(getOrderResponse);
    }

    private void itShouldReturnOrderWithSameMenuItems(GetOrderResponse getOrderResponse,
                                                      Set<OrderItem> orderItems) {
        Assertions.assertNotNull(getOrderResponse.orderId());
        Assertions.assertTrue(orderItems.containsAll(getOrderResponse.orderItems()));
    }

    private void itShouldReturnCreatedAndPreparedOrderStatuses(GetOrderResponse getOrderResponse) {
        Assertions.assertEquals(OrderStatus.CREATED.getStatus(), getOrderResponse.orderStatusRecordList().get(0).orderStatus());
        Assertions.assertEquals(OrderStatus.PREPARED.getStatus(), getOrderResponse.orderStatusRecordList().get(1).orderStatus());
    }
}
