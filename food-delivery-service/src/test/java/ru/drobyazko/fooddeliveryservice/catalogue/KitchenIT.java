package ru.drobyazko.fooddeliveryservice.catalogue;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.drobyazko.fooddeliveryservice.security.TestUserHelpers;
import ru.drobyazko.fooddeliveryservice.catalogue.api.CreateKitchenRequest;
import ru.drobyazko.fooddeliveryservice.catalogue.api.CreateKitchenResponse;
import ru.drobyazko.fooddeliveryservice.catalogue.api.GetKitchenResponse;
import ru.drobyazko.fooddeliveryservice.configuration.MockMvcConfiguration;
import ru.drobyazko.fooddeliveryservice.configuration.PostgreSQLContainerConfiguration;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import({PostgreSQLContainerConfiguration.class, MockMvcConfiguration.class})
class KitchenIT {
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setupUsers() throws Exception {
        TestUserHelpers.RegisterDefaultUsers(mockMvc);
    }

    @Test
    @Sql(scripts = "classpath:/TruncateAllTables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void whenICreateAKitchen() throws Exception {
        CreateKitchenRequest createKitchenRequest = new CreateKitchenRequest("test", "test");
        ResultActions createKitchenResultActions =
                KitchenApiHelper.sendCreateKitchenRequest(mockMvc, createKitchenRequest);

        createKitchenResultActions.andExpect(MockMvcResultMatchers.status().isCreated());

        CreateKitchenResponse response =
                KitchenApiHelper.mapCreateKitchenResponse(createKitchenResultActions);

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

    @Test
    @Sql(scripts = "classpath:/TruncateAllTables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void givenICreateAKitchen_WhenITryToGetAKitchen() throws Exception {
        CreateKitchenRequest createKitchenRequest = new CreateKitchenRequest("test", "test");
        CreateKitchenResponse createKitchenResponse = KitchenApiHelper.createKitchen(mockMvc, createKitchenRequest);

        ResultActions getKitchenResultActions =
                KitchenApiHelper.sendGetKitchenRequest(mockMvc, createKitchenResponse.id());
        getKitchenResultActions.andExpect(MockMvcResultMatchers.status().isOk());

        GetKitchenResponse getKitchenResponse =
                KitchenApiHelper.mapGetKitchenResponse(getKitchenResultActions);
        itShouldReturnTheSameKitchen(createKitchenRequest, getKitchenResponse);
    }

    private void itShouldReturnTheSameKitchen(
            CreateKitchenRequest createKitchenRequest,
            GetKitchenResponse getKitchenResponse) {
        Assertions.assertEquals(createKitchenRequest.name(), getKitchenResponse.name());
        Assertions.assertEquals(createKitchenRequest.address(), getKitchenResponse.address());
    }

    @Test
    @Sql(scripts = "classpath:/TruncateAllTables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void givenICreatedAFewKitchens_WhenITryToGetAllKitchens() throws Exception {
        List<CreateKitchenRequest> createKitchenRequestsList = List.of(
                new CreateKitchenRequest("test1", "test1"),
                new CreateKitchenRequest("test2", "test2")
        );
        for (CreateKitchenRequest createKitchenRequest : createKitchenRequestsList) {
            KitchenApiHelper.sendCreateKitchenRequest(mockMvc, createKitchenRequest);
        }

        ResultActions getAllKitchensResultActions =
                KitchenApiHelper.sendGetAllKitchensRequest(mockMvc);
        getAllKitchensResultActions.andExpect(MockMvcResultMatchers.status().isOk());

        TestPageResponse<GetKitchenResponse> getAllKitchenResponse =
                KitchenApiHelper.mapGetAllKitchensResponse(getAllKitchensResultActions);
        itShouldReturnAListOfSameKitchens(createKitchenRequestsList, getAllKitchenResponse.getContent());
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
    @Sql(scripts = "classpath:/TruncateAllTables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void givenICreatedAKitchen_WhenIDeleteAKitchen() throws Exception {
        CreateKitchenRequest createKitchenRequest = new CreateKitchenRequest("test", "test");
        CreateKitchenResponse createKitchenResponse = KitchenApiHelper.createKitchen(mockMvc, createKitchenRequest);

        ResultActions deleteKitchenResultActions =
                KitchenApiHelper.sendDeleteKitchenRequest(mockMvc, createKitchenResponse.id());

        deleteKitchenResultActions.andExpect(MockMvcResultMatchers.status().isNoContent());

        ResultActions getKitchenResultActions =
                KitchenApiHelper.sendGetKitchenRequest(mockMvc, createKitchenResponse.id());

        getKitchenResultActions.andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
