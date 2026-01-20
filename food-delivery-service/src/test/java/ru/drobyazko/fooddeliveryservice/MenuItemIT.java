package ru.drobyazko.fooddeliveryservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.drobyazko.fooddeliveryservice.catalogue.api.*;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import({PostgreSQLContainerConfiguration.class, MockMvcConfiguration.class})
class MenuItemIT {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
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
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
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
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
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
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
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
}
