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
import ru.drobyazko.fooddeliveryservice.catalogue.api.GetKitchenResponse;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(PostgreSQLContainerConfiguration.class)
class KitchenIT {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void whenICreateAKitchen() {
        CreateKitchenRequest createKitchenRequest = new CreateKitchenRequest("test", "test");
        ResponseSpec createKitchenResponseSpec =
                KitchenApiHelper.sendCreateKitchenRequest(webTestClient, createKitchenRequest);

        createKitchenResponseSpec.expectStatus().isCreated();

        CreateKitchenResponse response =
                KitchenApiHelper.mapCreateKitchenResponse(createKitchenResponseSpec);

        itShouldAllocateAnId(response);
        itShouldReturnTheSameKitchen(createKitchenRequest, response);
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
    void whenICreateAnInvalidKitchen() {
        CreateKitchenRequest createKitchenRequest = new CreateKitchenRequest("", null);
        ResponseSpec createKitchenResponseSpec =
                KitchenApiHelper.sendCreateKitchenRequest(webTestClient, createKitchenRequest);

        createKitchenResponseSpec.expectStatus().isBadRequest();
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenICreateAKitchen_WhenITryToGetAKitchen() {
        CreateKitchenRequest createKitchenRequest = new CreateKitchenRequest("test", "test");
        CreateKitchenResponse createKitchenResponse = KitchenApiHelper.createKitchen(webTestClient, createKitchenRequest);

        ResponseSpec getKitchenResponseSpec =
                KitchenApiHelper.sendGetKitchenRequest(webTestClient, createKitchenResponse.id());
        getKitchenResponseSpec.expectStatus().isOk();

        GetKitchenResponse getKitchenResponse =
                KitchenApiHelper.mapGetKitchenResponse(getKitchenResponseSpec);
        itShouldReturnTheSameKitchen(createKitchenRequest, getKitchenResponse);
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
                new CreateKitchenRequest("test1", "test1"),
                new CreateKitchenRequest("test2", "test2")
        );
        for (CreateKitchenRequest createKitchenRequest : createKitchenRequestsList) {
            KitchenApiHelper.sendCreateKitchenRequest(webTestClient, createKitchenRequest);
        }

        ResponseSpec getAllKitchensResponseSpec =
                KitchenApiHelper.sendGetAllKitchensRequest(webTestClient);
        getAllKitchensResponseSpec.expectStatus().isOk();

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
        CreateKitchenRequest createKitchenRequest = new CreateKitchenRequest("test", "test");
        CreateKitchenResponse createKitchenResponse = KitchenApiHelper.createKitchen(webTestClient, createKitchenRequest);

        ResponseSpec deleteKitchenResponseSpec =
                KitchenApiHelper.sendDeleteKitchenRequest(webTestClient, createKitchenResponse.id());

        deleteKitchenResponseSpec.expectStatus().isNoContent();

        ResponseSpec getKitchenResponseSpec =
                KitchenApiHelper.sendGetKitchenRequest(webTestClient, createKitchenResponse.id());

        getKitchenResponseSpec.expectStatus().isNotFound();
    }
}
