package ru.drobyazko.fooddeliveryservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.*;
import ru.drobyazko.fooddeliveryservice.catalogue.api.*;

import java.math.BigDecimal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(PostgreSQLContainerConfiguration.class)
class MenuItemIT {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenIHaveCreatedAKitchen_WhenITryToCreateAMenuItem() {
        CreateKitchenRequest createKitchenRequest =
                new CreateKitchenRequest("testName", "testAddress");
        ResponseSpec createKitchenResponseSpec =
                KitchenApiHelper.sendCreateKitchenRequest(webTestClient, createKitchenRequest);
        CreateKitchenResponse createKitchenResponse =
                KitchenApiHelper.mapCreateKitchenResponse(createKitchenResponseSpec);

        CreateMenuItemRequest createMenuItemRequest =
                new CreateMenuItemRequest(
                        createKitchenResponse.id(),
                        "testName",
                        "testAddress",
                        new BigDecimal(1));

        ResponseSpec createMenuItemResponseSpec =
                MenuItemApiHelper.sendCreateMenuItemRequest(webTestClient, createMenuItemRequest);

        itShouldReturnCreatedStatus(createMenuItemResponseSpec);

        CreateMenuItemResponse createMenuItemResponse =
                MenuItemApiHelper.mapCreateMenuItemResponse(createMenuItemResponseSpec);

        itShouldAllocateAnId(createMenuItemResponse);
        itShouldReturnTheSameMenuItem(createMenuItemRequest, createMenuItemResponse);
    }

    private void itShouldReturnCreatedStatus(ResponseSpec responseSpec) {
        responseSpec.expectStatus()
                .isCreated();
    }

    private void itShouldAllocateAnId(CreateMenuItemResponse response) {
        Assertions.assertNotNull(response.id());
    }

    private void itShouldReturnTheSameMenuItem(CreateMenuItemRequest request, CreateMenuItemResponse response) {
        Assertions.assertEquals(request.kitchenId(), response.kitchenId());
        Assertions.assertEquals(request.name(), response.name());
        Assertions.assertEquals(request.description(), response.description());
        Assertions.assertEquals(request.price(), response.price());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenICreatedAMenuItem_WhenITryToGetAMenuItem() {
        CreateKitchenRequest createKitchenRequest =
                new CreateKitchenRequest("testName", "testAddress");
        ResponseSpec createKitchenResponseSpec =
                KitchenApiHelper.sendCreateKitchenRequest(webTestClient, createKitchenRequest);
        CreateKitchenResponse createKitchenResponse =
                KitchenApiHelper.mapCreateKitchenResponse(createKitchenResponseSpec);

        CreateMenuItemRequest createMenuItemRequest =
                new CreateMenuItemRequest(
                        createKitchenResponse.id(),
                        "testName",
                        "testAddress",
                        new BigDecimal(1));

        ResponseSpec createMenuItemResponseSpec =
                MenuItemApiHelper.sendCreateMenuItemRequest(webTestClient, createMenuItemRequest);
        CreateMenuItemResponse createMenuItemResponse =
                MenuItemApiHelper.mapCreateMenuItemResponse(createMenuItemResponseSpec);

        ResponseSpec getMenuItemResponseSpec =
                MenuItemApiHelper.sendGetMenuItemRequest(webTestClient, createMenuItemResponse.id());
        itShouldReturnOkStatus(getMenuItemResponseSpec);

        GetMenuItemResponse getMenuItemResponse =
                MenuItemApiHelper.mapGetMenuItemResponse(getMenuItemResponseSpec);
        itShouldReturnTheSameMenuItem(createMenuItemRequest, getMenuItemResponse);
    }

    private void itShouldReturnOkStatus(ResponseSpec responseSpec) {
        responseSpec.expectStatus()
                .isOk();
    }

    private void itShouldReturnTheSameMenuItem(CreateMenuItemRequest request, GetMenuItemResponse response) {
        Assertions.assertEquals(request.kitchenId(), response.kitchenId());
        Assertions.assertEquals(request.name(), response.name());
        Assertions.assertEquals(request.description(), response.description());
        Assertions.assertEquals(request.price(), response.price());
    }

}
