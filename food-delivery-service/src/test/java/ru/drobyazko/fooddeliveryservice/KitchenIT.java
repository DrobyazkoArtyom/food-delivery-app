package ru.drobyazko.fooddeliveryservice;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.*;
import ru.drobyazko.fooddeliveryservice.dtos.requests.CreateKitchenRequest;
import ru.drobyazko.fooddeliveryservice.dtos.responses.GetKitchenResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(PostgreSQLContainerConfiguration.class)
class KitchenIT {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void whenICreateAKitchen() {
        CreateKitchenRequest createKitchenRequest = new CreateKitchenRequest("testName", "testAddress");
        ResponseSpec response = webTestClient.post()
                .uri("/kitchens")
                .bodyValue(createKitchenRequest)
                .exchange();

        itShouldReturnCreatedStatus(response);
        itShouldReturnAnId(response);
    }

    private void itShouldReturnCreatedStatus(ResponseSpec response) {
        response.expectStatus()
                .isCreated();
    }

    private void itShouldReturnAnId(ResponseSpec response) {
        response.expectBody(Long.class);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenICreateAKitchen_WhenIGetKitchen() {
        CreateKitchenRequest createKitchenRequest = new CreateKitchenRequest("testName", "testAddress");
        ResponseSpec createKitchenResponseSpec = webTestClient.post()
                .uri("/kitchens")
                .bodyValue(createKitchenRequest)
                .exchange();

        Long kitchenId = createKitchenResponseSpec.expectBody(Long.class).returnResult().getResponseBody();

        ResponseSpec getKitchenResponseSpec = webTestClient.get()
                .uri("/kitchens/" + kitchenId)
                .exchange();

        itShouldReturnAValidKitchen(getKitchenResponseSpec, createKitchenRequest);
    }

    private void itShouldReturnAValidKitchen(ResponseSpec getKitchensResponseSpec, CreateKitchenRequest createKitchenRequest) {
        getKitchensResponseSpec.expectBody(GetKitchenResponse.class)
                .value(getKitchenResponse -> {
                    Assertions.assertThat(getKitchenResponse.name().equals(createKitchenRequest.name()));
                    Assertions.assertThat(getKitchenResponse.address().equals(createKitchenRequest.address()));
                });
    }
}
