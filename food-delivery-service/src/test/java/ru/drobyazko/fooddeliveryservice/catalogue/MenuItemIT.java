package ru.drobyazko.fooddeliveryservice.catalogue;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.drobyazko.fooddeliveryservice.configuration.KafkaContainerConfiguration;
import ru.drobyazko.fooddeliveryservice.security.TestUserHelpers;
import ru.drobyazko.fooddeliveryservice.catalogue.api.*;
import ru.drobyazko.fooddeliveryservice.configuration.MockMvcConfiguration;
import ru.drobyazko.fooddeliveryservice.configuration.PostgreSQLContainerConfiguration;
import ru.drobyazko.fooddeliveryservice.security.UserApiHelper;
import ru.drobyazko.fooddeliveryservice.security.api.RegisterUserRequest;
import ru.drobyazko.fooddeliveryservice.security.domain.aggregate.Authority;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
// TODO: Catalogue domain does not care about kafka, so we do not actually need to run KafkaContainer
@Import({PostgreSQLContainerConfiguration.class, MockMvcConfiguration.class, KafkaContainerConfiguration.class})
class MenuItemIT {
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setupUsers() throws Exception {
        TestUserHelpers.RegisterDefaultUsers(mockMvc);
    }

    @Test
    @Sql(scripts = "classpath:/TruncateAllTables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void givenIHaveCreatedAKitchen_WhenITryToCreateAMenuItem() throws Exception {
        CreateKitchenRequest createKitchenRequest =
                new CreateKitchenRequest("test", "test");
        CreateKitchenResponse createKitchenResponse = KitchenApiHelper.createKitchen(mockMvc, createKitchenRequest);

        CreateMenuItemRequest createMenuItemRequest =
                new CreateMenuItemRequest(createKitchenResponse.id(), "test", "test", new BigDecimal(1));

        ResultActions createMenuItemResultActions =
                MenuItemApiHelper.sendCreateMenuItemRequest(mockMvc, createMenuItemRequest);

        createMenuItemResultActions.andExpect(MockMvcResultMatchers.status().isCreated());

        CreateMenuItemResponse createMenuItemResponse =
                MenuItemApiHelper.mapCreateMenuItemResponse(createMenuItemResultActions);

        itShouldAllocateAnId(createMenuItemResponse);
        itShouldReturnTheSameMenuItem(createMenuItemRequest, createMenuItemResponse);
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
    @Sql(scripts = "classpath:/TruncateAllTables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void givenICreatedAMenuItem_WhenITryToGetAMenuItem() throws Exception {
        CreateKitchenRequest createKitchenRequest = new CreateKitchenRequest("test", "test");
        CreateKitchenResponse createKitchenResponse = KitchenApiHelper.createKitchen(mockMvc, createKitchenRequest);

        CreateMenuItemRequest createMenuItemRequest =
                new CreateMenuItemRequest(createKitchenResponse.id(), "test", "test", new BigDecimal(1));

        CreateMenuItemResponse createMenuItemResponse =
                MenuItemApiHelper.createMenuItem(mockMvc, createMenuItemRequest);

        ResultActions getMenuItemResultActions =
                MenuItemApiHelper.sendGetMenuItemRequest(mockMvc, createMenuItemResponse.id());
        getMenuItemResultActions.andExpect(MockMvcResultMatchers.status().isOk());

        GetMenuItemResponse getMenuItemResponse =
                MenuItemApiHelper.mapGetMenuItemResponse(getMenuItemResultActions);
        itShouldReturnTheSameMenuItem(createMenuItemRequest, getMenuItemResponse);
    }

    private void itShouldReturnTheSameMenuItem(CreateMenuItemRequest request, GetMenuItemResponse response) {
        Assertions.assertEquals(request.kitchenId(), response.kitchenId());
        Assertions.assertEquals(request.name(), response.name());
        Assertions.assertEquals(request.description(), response.description());
        Assertions.assertEquals(request.price(), response.price());
    }

    @Test
    @Sql(scripts = "classpath:/TruncateAllTables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void givenICreatedAFewMenuItems_WhenITryToGetKitchenMenu() throws Exception {
        CreateKitchenRequest createKitchenRequest = new CreateKitchenRequest("test", "test");
        CreateKitchenResponse createKitchenResponse = KitchenApiHelper.createKitchen(mockMvc, createKitchenRequest);

        List<CreateMenuItemRequest> createMenuItemRequests = List.of(
                new CreateMenuItemRequest(createKitchenResponse.id(), "test1", "test1", new BigDecimal(1)),
                new CreateMenuItemRequest(createKitchenResponse.id(), "test2", "test2", new BigDecimal(1))
        );

        for (CreateMenuItemRequest createMenuItemRequest : createMenuItemRequests) {
            MenuItemApiHelper.sendCreateMenuItemRequest(mockMvc, createMenuItemRequest);
        }

        ResultActions getMenuResultActions =
                MenuItemApiHelper.sendGetMenuRequest(mockMvc, createKitchenResponse.id());
        getMenuResultActions.andExpect(MockMvcResultMatchers.status().isOk());

        List<GetMenuItemResponse> getMenuResponse =
                MenuItemApiHelper.mapGetMenuResponse(getMenuResultActions);

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
    @Sql(scripts = "classpath:/TruncateAllTables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void givenICreatedAMenuItem_WhenITryToDeleteIt() throws Exception {
        CreateKitchenRequest createKitchenRequest = new CreateKitchenRequest("test", "test");
        CreateKitchenResponse createKitchenResponse = KitchenApiHelper.createKitchen(mockMvc, createKitchenRequest);

        CreateMenuItemRequest createMenuItemRequest =
                new CreateMenuItemRequest(createKitchenResponse.id(), "test", "test", new BigDecimal(1));

        CreateMenuItemResponse createMenuItemResponse =
                MenuItemApiHelper.createMenuItem(mockMvc, createMenuItemRequest);

        ResultActions deleteMenuItemResultActions =
                MenuItemApiHelper.sendDeleteMenuItemRequest(mockMvc, createMenuItemResponse.id());

        deleteMenuItemResultActions.andExpect(MockMvcResultMatchers.status().isNoContent());

        ResultActions getMenuItemResultActions =
                MenuItemApiHelper.sendGetMenuItemRequest(mockMvc, createMenuItemResponse.id());

        getMenuItemResultActions.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Sql(scripts = "classpath:/TruncateAllTables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void givenInvalidData_WhenITryToCreateAMenuItem_ThenBadRequest() throws Exception {
        CreateKitchenResponse createKitchenResponse =
                KitchenApiHelper.createKitchen(mockMvc, new CreateKitchenRequest("test", "test"));

        CreateMenuItemRequest createMenuItemRequest =
                new CreateMenuItemRequest(createKitchenResponse.id(), "   ", "test", null);

        MenuItemApiHelper.sendCreateMenuItemRequest(mockMvc, createMenuItemRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @Sql(scripts = "classpath:/TruncateAllTables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void givenNonexistentKitchen_WhenITryToGetKitchenMenu_ThenNotFound() throws Exception {
        MenuItemApiHelper.sendGetMenuRequest(mockMvc, 999999L)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Sql(scripts = "classpath:/TruncateAllTables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void givenKitchenOwnedByAnotherKitchenUser_WhenITryToCreateAMenuItem_ThenForbidden() throws Exception {
        CreateKitchenResponse createKitchenResponse =
                KitchenApiHelper.createKitchen(mockMvc, new CreateKitchenRequest("test", "test"));

        RegisterUserRequest registerKitchenRequest =
                new RegisterUserRequest("another-kitchen",
                        TestUserHelpers.PASSWORD_NOOP_PREFIX + "another-kitchen",
                        Set.of(Authority.KITCHEN));
        UserApiHelper.sendRegisterUserRequest(mockMvc, registerKitchenRequest);
        CreateMenuItemRequest createMenuItemRequest =
                new CreateMenuItemRequest(createKitchenResponse.id(), "test", "test", new BigDecimal(1));

        MenuItemApiHelper.sendCreateMenuItemRequest(mockMvc,
                        createMenuItemRequest,
                        SecurityMockMvcRequestPostProcessors.httpBasic("another-kitchen", "another-kitchen"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @Sql(scripts = "classpath:/TruncateAllTables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void givenMenuItemOwnedByAnotherKitchenUser_WhenITryToDeleteIt_ThenForbidden() throws Exception {
        CreateKitchenResponse createKitchenResponse =
                KitchenApiHelper.createKitchen(mockMvc, new CreateKitchenRequest("test", "test"));
        CreateMenuItemResponse createMenuItemResponse = MenuItemApiHelper.createMenuItem(
                mockMvc,
                new CreateMenuItemRequest(createKitchenResponse.id(), "test", "test", new BigDecimal(1))
        );

        RegisterUserRequest registerKitchenRequest =
                new RegisterUserRequest("another-kitchen",
                        TestUserHelpers.PASSWORD_NOOP_PREFIX + "another-kitchen",
                        Set.of(Authority.KITCHEN));
        UserApiHelper.sendRegisterUserRequest(mockMvc, registerKitchenRequest);

        MenuItemApiHelper.sendDeleteMenuItemRequest(mockMvc,
                        createMenuItemResponse.id(),
                        SecurityMockMvcRequestPostProcessors.httpBasic("another-kitchen", "another-kitchen"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @Sql(scripts = "classpath:/TruncateAllTables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void givenIDeleteAMenuItem_WhenITryToGetKitchenMenu_ThenDeletedItemIsExcluded() throws Exception {
        CreateKitchenResponse createKitchenResponse =
                KitchenApiHelper.createKitchen(mockMvc, new CreateKitchenRequest("test", "test"));
        CreateMenuItemResponse deletedMenuItemResponse = MenuItemApiHelper.createMenuItem(
                mockMvc,
                new CreateMenuItemRequest(createKitchenResponse.id(), "test1", "test1", new BigDecimal(1))
        );
        CreateMenuItemResponse remainingMenuItemResponse = MenuItemApiHelper.createMenuItem(
                mockMvc,
                new CreateMenuItemRequest(createKitchenResponse.id(), "test2", "test2", new BigDecimal(2))
        );

        MenuItemApiHelper.sendDeleteMenuItemRequest(mockMvc, deletedMenuItemResponse.id())
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        ResultActions getMenuResultActions =
                MenuItemApiHelper.sendGetMenuRequest(mockMvc, createKitchenResponse.id());
        getMenuResultActions.andExpect(MockMvcResultMatchers.status().isOk());

        List<GetMenuItemResponse> getMenuResponse =
                MenuItemApiHelper.mapGetMenuResponse(getMenuResultActions);

        Assertions.assertEquals(1, getMenuResponse.size());
        Assertions.assertEquals(remainingMenuItemResponse.id(), getMenuResponse.get(0).id());
        Assertions.assertEquals(remainingMenuItemResponse.name(), getMenuResponse.get(0).name());
        Assertions.assertEquals(remainingMenuItemResponse.description(), getMenuResponse.get(0).description());
        Assertions.assertEquals(remainingMenuItemResponse.price(), getMenuResponse.get(0).price());
    }
}
