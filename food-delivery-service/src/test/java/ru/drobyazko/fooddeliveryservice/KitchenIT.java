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

//TODO: some operations are used in several classes, can create a separate utilities class for them (itShouldReturnCreatedStatus)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(PostgreSQLContainerConfiguration.class)
class KitchenIT {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void whenICreateAKitchen() {
        CreateKitchenRequest createKitchenRequest = new CreateKitchenRequest("testName", "testAddress");
        ResponseSpec createKitchenResponseSpec =
                KitchenApiHelper.sendCreateKitchenRequest(webTestClient, createKitchenRequest);

        itShouldReturnCreatedStatus(createKitchenResponseSpec);

        CreateKitchenResponse response =
                KitchenApiHelper.mapCreateKitchenResponse(createKitchenResponseSpec);

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
        ResponseSpec createKitchenResponseSpec =
                KitchenApiHelper.sendCreateKitchenRequest(webTestClient, createKitchenRequest);

        itShouldReturnBadRequestStatus(createKitchenResponseSpec);
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
                KitchenApiHelper.sendCreateKitchenRequest(webTestClient, createKitchenRequest);

        CreateKitchenResponse createKitchenResponse =
                KitchenApiHelper.mapCreateKitchenResponse(createKitchenResponseSpec);

        ResponseSpec getKitchenResponseSpec =
                KitchenApiHelper.sendGetKitchenRequest(webTestClient, createKitchenResponse.id());
        itShouldReturnOkStatus(getKitchenResponseSpec);

        GetKitchenResponse getKitchenResponse =
                KitchenApiHelper.mapGetKitchenResponse(getKitchenResponseSpec);
        itShouldReturnTheSameKitchen(createKitchenRequest, getKitchenResponse);
    }

    private void itShouldReturnOkStatus(ResponseSpec response) {
        response.expectStatus()
                .isOk();
    }

    private void itShouldReturnTheSameKitchen(
            CreateKitchenRequest createKitchenRequest,
            GetKitchenResponse getKitchenResponse) {
        Assertions.assertEquals(createKitchenRequest.name(), getKitchenResponse.name());
        Assertions.assertEquals(createKitchenRequest.address(), getKitchenResponse.address());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenICreatedAFewKitchens_WhenITryToGetAllKitchens() {
        List<CreateKitchenRequest> createKitchenRequestsList = List.of(
                new CreateKitchenRequest("testName", "testAddress"),
                new CreateKitchenRequest("testNameSecond", "testAddressSecond")
        );
        for (CreateKitchenRequest createKitchenRequest : createKitchenRequestsList) {
            KitchenApiHelper.sendCreateKitchenRequest(webTestClient, createKitchenRequest);
        }

        ResponseSpec getAllKitchensResponseSpec =
                KitchenApiHelper.sendGetAllKitchensRequest(webTestClient);
        itShouldReturnOkStatus(getAllKitchensResponseSpec);

        List<GetKitchenResponse> getAllKitchenResponse =
                KitchenApiHelper.mapGetAllKitchensResponse(getAllKitchensResponseSpec);
        itShouldReturnAListOfSameKitchens(createKitchenRequestsList, getAllKitchenResponse);
    }

    private void itShouldReturnAListOfSameKitchens(
            List<CreateKitchenRequest> createKitchenRequests,
            List<GetKitchenResponse> getAllKitchensResponse) {
        Assertions.assertEquals(createKitchenRequests.get(0).name(), getAllKitchensResponse.get(0).name());
        Assertions.assertEquals(createKitchenRequests.get(0).address(), getAllKitchensResponse.get(0).address());
        Assertions.assertEquals(createKitchenRequests.get(1).name(), getAllKitchensResponse.get(1).name());
        Assertions.assertEquals(createKitchenRequests.get(1).address(), getAllKitchensResponse.get(1).address());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenICreatedAKitchen_WhenIDeleteAKitchen() {
        CreateKitchenRequest createKitchenRequest = new CreateKitchenRequest("testName", "testAddress");
        ResponseSpec createKitchenResponseSpec =
                KitchenApiHelper.sendCreateKitchenRequest(webTestClient, createKitchenRequest);

        CreateKitchenResponse createKitchenResponse =
                KitchenApiHelper.mapCreateKitchenResponse(createKitchenResponseSpec);

        ResponseSpec deleteKitchenResponseSpec =
                KitchenApiHelper.sendDeleteKitchenRequest(webTestClient, createKitchenResponse.id());

        itShouldReturnNoContentStatus(deleteKitchenResponseSpec);

        ResponseSpec getKitchenResponseSpec =
                KitchenApiHelper.sendGetKitchenRequest(webTestClient, createKitchenResponse.id());

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
