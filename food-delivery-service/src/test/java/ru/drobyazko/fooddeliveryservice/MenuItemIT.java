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

import java.math.BigDecimal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(PostgreSQLContainerConfiguration.class)
class MenuItemIT {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenIHaveCreatedAKitchen_WhenITryToCreateAMenuItem() {
        CreateKitchenResponse createKitchenResponse = createKitchen();

        CreateMenuItemRequest createMenuItemRequest =
                new CreateMenuItemRequest(
                        createKitchenResponse.id(),
                        "testName",
                        "testAddress",
                        new BigDecimal(1));

        ResponseSpec createMenuItemResponseSpec =
                webTestClient.post()
                        .uri("/menuItems")
                        .bodyValue(createMenuItemRequest)
                        .exchange();

        itShouldReturnCreatedStatus(createMenuItemResponseSpec);

        CreateMenuItemResponse createMenuItemResponse =
                createMenuItemResponseSpec.expectBody(CreateMenuItemResponse.class)
                        .returnResult()
                        .getResponseBody();

        itShouldAllocateAnId(createMenuItemResponse);
        itShouldReturnTheSameMenuItem(createMenuItemRequest, createMenuItemResponse);
    }

    private CreateKitchenResponse createKitchen() {
        CreateKitchenRequest createKitchenRequest =
                new CreateKitchenRequest("testName", "testAddress");
        return webTestClient.post()
                .uri("/kitchens")
                .bodyValue(createKitchenRequest)
                .exchange()
                .expectBody(CreateKitchenResponse.class)
                .returnResult()
                .getResponseBody();
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

}
