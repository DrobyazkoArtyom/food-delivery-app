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
import ru.drobyazko.fooddeliveryservice.catalogue.api.CreateKitchenRequest;
import ru.drobyazko.fooddeliveryservice.catalogue.api.CreateKitchenResponse;
import ru.drobyazko.fooddeliveryservice.catalogue.api.GetKitchenResponse;

import java.util.List;
import java.util.stream.Stream;

//TODO: some operations are duplicated, can create a separate class for them (for example a CreateKitchenRequest is done in every test)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(PostgreSQLContainerConfiguration.class)
class KitchenIT {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void whenICreateAKitchen() {
        CreateKitchenRequest createKitchenRequest = new CreateKitchenRequest("testName", "testAddress");
        ResponseSpec responseSpec =
                webTestClient.post()
                        .uri("/kitchens")
                        .bodyValue(createKitchenRequest)
                        .exchange();

        itShouldReturnCreatedStatus(responseSpec);

        CreateKitchenResponse response =
                responseSpec.expectBody(CreateKitchenResponse.class)
                        .returnResult()
                        .getResponseBody();

        itShouldAllocateAnId(response);
        itShouldReturnTheSameKitchen(createKitchenRequest, response);
    }

    private void itShouldReturnCreatedStatus(ResponseSpec responseSpec) {
        responseSpec.expectStatus()
                .isCreated();
    }

    private void itShouldAllocateAnId(CreateKitchenResponse response) {
        Assertions.assertNotNull(response.id());
    }

    private void itShouldReturnTheSameKitchen(CreateKitchenRequest createKitchenRequest, CreateKitchenResponse response) {
        Assertions.assertEquals(createKitchenRequest.name(), response.name());
        Assertions.assertEquals(createKitchenRequest.address(), response.address());
    }

    //TODO: should probably make this one a unit test in the controller slice
    @Test
    void WhenICreateAnInvalidKitchen() {
        CreateKitchenRequest createKitchenRequest = new CreateKitchenRequest("", null);
        ResponseSpec response =
                webTestClient.post().uri("/kitchens")
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
                new CreateKitchenRequest("testNameSecond", "testAddressSecond")
        );
    }

    @ParameterizedTest
    @MethodSource("createKitchenRequestStream")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenICreateAKitchen_WhenITryToGetAKitchen(CreateKitchenRequest createKitchenRequest) {
        ResponseSpec createKitchenResponseSpec =
                webTestClient.post()
                        .uri("/kitchens")
                        .bodyValue(createKitchenRequest)
                        .exchange();

        CreateKitchenResponse createKitchenResponse =
                createKitchenResponseSpec.expectBody(CreateKitchenResponse.class)
                        .returnResult()
                        .getResponseBody();

        ResponseSpec getKitchenResponseSpec =
                webTestClient.get()
                        .uri("/kitchens/" + createKitchenResponse.id())
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
        for (CreateKitchenRequest createKitchenRequest : createKitchenRequestsList) {
            ResponseSpec createKitchenResponseSpec =
                    webTestClient.post()
                            .uri("/kitchens")
                            .bodyValue(createKitchenRequest)
                            .exchange();
        }

        ResponseSpec getAllKitchensResponseSpec =
                webTestClient.get()
                        .uri("/kitchens")
                        .exchange();

        itShouldReturnOkStatus(getAllKitchensResponseSpec);

        itShouldReturnAListOfSameKitchens(getAllKitchensResponseSpec, createKitchenRequestsList);
    }

    private GetKitchenResponse buildGetKitchenResponse(CreateKitchenResponse createKitchenResponse,
                                                       CreateKitchenRequest createKitchenRequest) {
        return new GetKitchenResponse(createKitchenResponse.id(), createKitchenRequest.name(), createKitchenRequest.address());
    }

    private void itShouldReturnAListOfSameKitchens(ResponseSpec responseSpec,
                                                   List<CreateKitchenRequest> createKitchenRequests) {
        responseSpec.expectBodyList(GetKitchenResponse.class)
                .value(getKitchenResponsesActual -> {
                    Assertions.assertEquals(createKitchenRequests.get(0).name(), getKitchenResponsesActual.get(0).name());
                    Assertions.assertEquals(createKitchenRequests.get(0).address(), getKitchenResponsesActual.get(0).address());
                    Assertions.assertEquals(createKitchenRequests.get(1).name(), getKitchenResponsesActual.get(1).name());
                    Assertions.assertEquals(createKitchenRequests.get(1).address(), getKitchenResponsesActual.get(1).address());
                });
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenICreatedAKitchen_WhenIDeleteAKitchen() {
        CreateKitchenRequest createKitchenRequest = new CreateKitchenRequest("testName", "testAddress");
        ResponseSpec createKitchenResponseSpec =
                webTestClient.post()
                        .uri("/kitchens")
                        .bodyValue(createKitchenRequest)
                        .exchange();

        CreateKitchenResponse createKitchenResponse =
                createKitchenResponseSpec.expectBody(CreateKitchenResponse.class)
                        .returnResult()
                        .getResponseBody();

        ResponseSpec deleteKitchenResponseSpec =
                webTestClient.delete()
                        .uri("/kitchens/" + createKitchenResponse.id())
                        .exchange();

        itShouldReturnNoContentStatus(deleteKitchenResponseSpec);

        ResponseSpec getKitchenResponseSpec =
                webTestClient.get()
                        .uri("/kitchens/" + createKitchenResponse.id())
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
