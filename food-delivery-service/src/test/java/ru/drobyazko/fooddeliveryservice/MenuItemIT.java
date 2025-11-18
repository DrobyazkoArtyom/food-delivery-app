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
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(PostgreSQLContainerConfiguration.class)
class MenuItemIT {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenIHaveCreatedAKitchen_WhenITryToCreateAMenuItem() {
        CreateKitchenRequest createKitchenRequest =
                new CreateKitchenRequest("testName", "testDescription");
        ResponseSpec createKitchenResponseSpec =
                KitchenApiHelper.sendCreateKitchenRequest(webTestClient, createKitchenRequest);
        CreateKitchenResponse createKitchenResponse =
                KitchenApiHelper.mapCreateKitchenResponse(createKitchenResponseSpec);

        CreateMenuItemRequest createMenuItemRequest =
                new CreateMenuItemRequest(
                        createKitchenResponse.id(),
                        "testName",
                        "testDescription",
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
                new CreateKitchenRequest("testName", "testDescription");
        ResponseSpec createKitchenResponseSpec =
                KitchenApiHelper.sendCreateKitchenRequest(webTestClient, createKitchenRequest);
        CreateKitchenResponse createKitchenResponse =
                KitchenApiHelper.mapCreateKitchenResponse(createKitchenResponseSpec);

        CreateMenuItemRequest createMenuItemRequest =
                new CreateMenuItemRequest(
                        createKitchenResponse.id(),
                        "testName",
                        "testDescription",
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

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenICreatedAFewMenuItems_WhenITryToGetKitchenMenu() {
        CreateKitchenRequest createKitchenRequest =
                new CreateKitchenRequest("testName", "testDescription");
        ResponseSpec createKitchenResponseSpec =
                KitchenApiHelper.sendCreateKitchenRequest(webTestClient, createKitchenRequest);
        CreateKitchenResponse createKitchenResponse =
                KitchenApiHelper.mapCreateKitchenResponse(createKitchenResponseSpec);

        List<CreateMenuItemRequest> createMenuItemRequests = List.of(
                new CreateMenuItemRequest(
                        createKitchenResponse.id(),
                        "testName",
                        "testDescription",
                        new BigDecimal(1)),
                new CreateMenuItemRequest(
                        createKitchenResponse.id(),
                        "testNameSecond",
                        "testDescriptionSecond",
                        new BigDecimal(1))
        );

        for (CreateMenuItemRequest createMenuItemRequest : createMenuItemRequests) {
            MenuItemApiHelper.sendCreateMenuItemRequest(webTestClient, createMenuItemRequest);
        }

        ResponseSpec getMenuResponseSpec =
                MenuItemApiHelper.sendGetMenuRequest(webTestClient, createKitchenResponse.id());
        itShouldReturnOkStatus(getMenuResponseSpec);

        List<GetMenuItemResponse> getMenuResponse =
                MenuItemApiHelper.mapGetMenuResponse(getMenuResponseSpec);

        itShouldReturnAListOfSameMenuItems(createMenuItemRequests, getMenuResponse);
    }

    private void itShouldReturnAListOfSameMenuItems(List<CreateMenuItemRequest> createMenuItemRequests,
                                                    List<GetMenuItemResponse> getAllMenuItemsResponse) {
        Assertions.assertEquals(createMenuItemRequests.get(0).kitchenId(), getAllMenuItemsResponse.get(0).kitchenId());
        Assertions.assertEquals(createMenuItemRequests.get(0).name(), getAllMenuItemsResponse.get(0).name());
        Assertions.assertEquals(createMenuItemRequests.get(0).description(), getAllMenuItemsResponse.get(0).description());
        Assertions.assertEquals(createMenuItemRequests.get(0).price(), getAllMenuItemsResponse.get(0).price());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenIMenuItem_WhenITryToDeleteIt() {
        CreateKitchenRequest createKitchenRequest =
                new CreateKitchenRequest("testName", "testDescription");
        ResponseSpec createKitchenResponseSpec =
                KitchenApiHelper.sendCreateKitchenRequest(webTestClient, createKitchenRequest);
        CreateKitchenResponse createKitchenResponse =
                KitchenApiHelper.mapCreateKitchenResponse(createKitchenResponseSpec);

        CreateMenuItemRequest createMenuItemRequest =
                new CreateMenuItemRequest(
                        createKitchenResponse.id(),
                        "testName",
                        "testDescription",
                        new BigDecimal(1));

        ResponseSpec createMenuItemResponseSpec =
                MenuItemApiHelper.sendCreateMenuItemRequest(webTestClient, createMenuItemRequest);
        CreateMenuItemResponse createMenuItemResponse =
                MenuItemApiHelper.mapCreateMenuItemResponse(createMenuItemResponseSpec);

        ResponseSpec deleteMenuItemResponseSpec =
                MenuItemApiHelper.deleteMenuItemRequest(webTestClient, createMenuItemResponse.id());

        itShouldReturnNoContentStatus(deleteMenuItemResponseSpec);

        ResponseSpec getMenuItemResponseSpec =
                MenuItemApiHelper.sendGetMenuItemRequest(webTestClient, createMenuItemResponse.id());

        itShouldReturnNotFound(getMenuItemResponseSpec);
    }

    private void itShouldReturnNoContentStatus(ResponseSpec responseSpec) {
        responseSpec.expectStatus()
                .isNoContent();
    }

    private void itShouldReturnNotFound(ResponseSpec responseSpec) {
        responseSpec.expectStatus()
                .isNotFound();
    }
}
