package ru.drobyazko.fooddeliveryservice;

import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.*;
import ru.drobyazko.fooddeliveryservice.catalogue.api.CreateMenuItemRequest;
import ru.drobyazko.fooddeliveryservice.catalogue.api.CreateMenuItemResponse;
import ru.drobyazko.fooddeliveryservice.catalogue.api.GetMenuItemResponse;

import java.util.List;

public class MenuItemApiHelper {
    public static ResponseSpec sendCreateMenuItemRequest(
            WebTestClient webTestClient,
            CreateMenuItemRequest createMenuItemRequest) {
        return webTestClient.post()
                .uri("/menuItems")
                .bodyValue(createMenuItemRequest)
                .exchange();
    }

    public static CreateMenuItemResponse mapCreateMenuItemResponse(ResponseSpec createMenuItemResponseSpec) {
        return createMenuItemResponseSpec.expectBody(CreateMenuItemResponse.class)
                .returnResult()
                .getResponseBody();
    }

    public static CreateMenuItemResponse createMenuItem(WebTestClient webTestClient, CreateMenuItemRequest createMenuItemRequest) {
        ResponseSpec responseSpec = sendCreateMenuItemRequest(webTestClient, createMenuItemRequest);
        return mapCreateMenuItemResponse(responseSpec);
    }

    public static ResponseSpec sendGetMenuItemRequest(WebTestClient webTestClient, Long id) {
        return webTestClient.get()
                .uri("/menuItems/" + id)
                .exchange();
    }

    public static GetMenuItemResponse mapGetMenuItemResponse(ResponseSpec getMenuItemResponseSpec) {
        return getMenuItemResponseSpec.expectBody(GetMenuItemResponse.class)
                .returnResult()
                .getResponseBody();
    }

    public static ResponseSpec sendGetMenuRequest(
            WebTestClient webTestClient,
            Long kitchenId) {
        return webTestClient.get()
                .uri("/menuItems?kitchenId=" + kitchenId)
                .exchange();
    }

    public static List<GetMenuItemResponse> mapGetMenuResponse(ResponseSpec getAllMenuItemsResponseSpec) {
        return getAllMenuItemsResponseSpec.expectBodyList(GetMenuItemResponse.class)
                .returnResult()
                .getResponseBody();
    }

    public static ResponseSpec sendDeleteMenuItemRequest(WebTestClient webTestClient, Long id) {
        return webTestClient.delete()
                .uri("/menuItems/" + id)
                .exchange();
    }

}
