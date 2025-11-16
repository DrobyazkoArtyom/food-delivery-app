package ru.drobyazko.fooddeliveryservice;

import org.jetbrains.annotations.NotNull;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.*;
import ru.drobyazko.fooddeliveryservice.catalogue.api.CreateKitchenRequest;
import ru.drobyazko.fooddeliveryservice.catalogue.api.CreateKitchenResponse;
import ru.drobyazko.fooddeliveryservice.catalogue.api.GetKitchenResponse;

import java.util.List;

public class KitchenApiHelper {
    public static ResponseSpec sendCreateKitchenRequest(
            WebTestClient webTestClient,
            CreateKitchenRequest createKitchenRequest) {
        return webTestClient.post()
                .uri("/kitchens")
                .bodyValue(createKitchenRequest)
                .exchange();
    }

    public static CreateKitchenResponse mapCreateKitchenResponse(ResponseSpec responseSpec) {
        return responseSpec.expectBody(CreateKitchenResponse.class)
                .returnResult()
                .getResponseBody();
    }

    public static ResponseSpec sendGetKitchenRequest(WebTestClient webTestClient, Long id) {
        return webTestClient.get()
                .uri("/kitchens/" + id)
                .exchange();
    }

    public static GetKitchenResponse mapGetKitchenResponse(ResponseSpec responseSpec) {
        return responseSpec.expectBody(GetKitchenResponse.class)
                .returnResult()
                .getResponseBody();
    }

    public static ResponseSpec sendGetAllKitchensRequest(WebTestClient webTestClient) {
        return webTestClient.get()
                .uri("/kitchens")
                .exchange();
    }

    public static List<GetKitchenResponse> mapGetAllKitchensResponse(ResponseSpec responseSpec) {
        return responseSpec.expectBodyList(GetKitchenResponse.class)
                .returnResult()
                .getResponseBody();
    }

    public static ResponseSpec sendDeleteKitchenRequest(WebTestClient webTestClient, Long id) {
        return webTestClient.delete()
                .uri("/kitchens/" + id)
                .exchange();
    }
}
