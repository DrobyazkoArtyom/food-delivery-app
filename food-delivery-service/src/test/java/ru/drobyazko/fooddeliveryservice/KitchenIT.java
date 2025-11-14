package ru.drobyazko.fooddeliveryservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.*;
import ru.drobyazko.fooddeliveryservice.dtos.requests.CreateKitchenRequest;
import ru.drobyazko.fooddeliveryservice.dtos.responses.GetKitchenResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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

    private void itShouldReturnCreatedStatus(ResponseSpec responseSpec) {
        responseSpec.expectStatus()
                .isCreated();
    }

    private void itShouldReturnAnId(ResponseSpec responseSpec) {
        responseSpec.expectBody(Long.class)
                .value(id -> {
                    Assertions.assertNotNull(id);
                });
    }

    @Test
    void WhenICreateAnInvalidKitchen() {
        CreateKitchenRequest createKitchenRequest = new CreateKitchenRequest("", null);
        ResponseSpec response = webTestClient.post()
                .uri("/kitchens")
                .bodyValue(createKitchenRequest)
                .exchange();

        itShouldReturnBadRequestStatus(response);
    }

    private void itShouldReturnBadRequestStatus(ResponseSpec response) {
        response.expectStatus()
                .isBadRequest();
    }

    private static Stream<CreateKitchenRequest> createKitchenRequestStream() {
        return Stream.of(
                new CreateKitchenRequest("testName", "testAddress"),
                new CreateKitchenRequest("testNameSecond", "testAddressSecond"));
    }

    @ParameterizedTest
    @MethodSource("createKitchenRequestStream")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenICreateAKitchen_WhenITryToGetAKitchen(CreateKitchenRequest createKitchenRequest) {
        ResponseSpec createKitchenResponseSpec = webTestClient.post()
                .uri("/kitchens")
                .bodyValue(createKitchenRequest)
                .exchange();

        Long kitchenId = createKitchenResponseSpec.expectBody(Long.class).returnResult().getResponseBody();

        ResponseSpec getKitchenResponseSpec = webTestClient.get()
                .uri("/kitchens/" + kitchenId)
                .exchange();

        itShouldReturnOkStatus(getKitchenResponseSpec);
        itShouldReturnTheSameKitchen(getKitchenResponseSpec, createKitchenRequest);
    }

    private void itShouldReturnOkStatus(ResponseSpec response) {
        response.expectStatus()
                .isOk();
    }

    private void itShouldReturnTheSameKitchen(ResponseSpec responseSpec, CreateKitchenRequest createKitchenRequest) {
        responseSpec.expectBody(GetKitchenResponse.class)
                .value(getKitchenResponse -> {
                    Assertions.assertEquals(createKitchenRequest.name(), getKitchenResponse.name());
                    Assertions.assertEquals(createKitchenRequest.address(), getKitchenResponse.address());
                });
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenICreatedAFewKitchens_WhenITryToGetAllKitchens() {
        List<CreateKitchenRequest> createKitchenRequestsList = List.of(
                new CreateKitchenRequest("testName", "testAddress"),
                new CreateKitchenRequest("testNameSecond", "testAddressSecond")
        );
        List<GetKitchenResponse> getKitchenResponses = new ArrayList<>();
        for (CreateKitchenRequest createKitchenRequest : createKitchenRequestsList) {
            ResponseSpec createKitchenResponseSpec = webTestClient.post()
                    .uri("/kitchens")
                    .bodyValue(createKitchenRequest)
                    .exchange();

            getKitchenResponses.add(buildGetKitchenResponse(
                    createKitchenResponseSpec.expectBody(Long.class).returnResult().getResponseBody(),
                    createKitchenRequest));
        }

        ResponseSpec getAllKitchensResponseSpec = webTestClient.get()
                .uri("/kitchens")
                .exchange();

        itShouldReturnOkStatus(getAllKitchensResponseSpec);
        itShouldReturnAListOfSameKitchens(getAllKitchensResponseSpec, getKitchenResponses);
    }

    private GetKitchenResponse buildGetKitchenResponse(Long id, CreateKitchenRequest createKitchenRequest) {
        return new GetKitchenResponse(id, createKitchenRequest.name(), createKitchenRequest.address());
    }

    private void itShouldReturnAListOfSameKitchens(ResponseSpec responseSpec,
                                                   List<GetKitchenResponse> getKitchenResponsesExpected) {
        responseSpec.expectBodyList(GetKitchenResponse.class)
                .value(getKitchenResponsesActual -> {
                    Assertions.assertEquals(getKitchenResponsesExpected.get(0), getKitchenResponsesActual.get(0));
                    Assertions.assertEquals(getKitchenResponsesExpected.get(1), getKitchenResponsesActual.get(1));
                });
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenICreatedAKitchen_WhenIDeleteAKitchen() {
        CreateKitchenRequest createKitchenRequest = new CreateKitchenRequest("testName", "testAddress");
        ResponseSpec createKitchenResponseSpec = webTestClient.post()
                .uri("/kitchens")
                .bodyValue(createKitchenRequest)
                .exchange();

        Long kitchenId = createKitchenResponseSpec.expectBody(Long.class).returnResult().getResponseBody();

        ResponseSpec deleteKitchenResponseSpec = webTestClient.delete()
                .uri("/kitchens/" + kitchenId)
                .exchange();

        itShouldReturnNoContentStatus(deleteKitchenResponseSpec);

        ResponseSpec getKitchenResponseSpec = webTestClient.get()
                .uri("/kitchens/" + kitchenId)
                .exchange();

        itShouldReturnNotFound(getKitchenResponseSpec);
    }

    private void itShouldReturnNotFound(ResponseSpec responseSpec) {
        responseSpec.expectStatus()
                .isNotFound();
    }

    private void itShouldReturnNoContentStatus(ResponseSpec responseSpec) {
        responseSpec.expectStatus()
                .isNoContent();
    }

}
