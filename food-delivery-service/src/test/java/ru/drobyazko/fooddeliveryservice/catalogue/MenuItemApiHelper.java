package ru.drobyazko.fooddeliveryservice.catalogue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.drobyazko.fooddeliveryservice.security.TestUserHelpers;
import ru.drobyazko.fooddeliveryservice.catalogue.api.CreateMenuItemRequest;
import ru.drobyazko.fooddeliveryservice.catalogue.api.CreateMenuItemResponse;
import ru.drobyazko.fooddeliveryservice.catalogue.api.GetMenuItemResponse;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class MenuItemApiHelper {
    public static ResultActions sendCreateMenuItemRequest(MockMvc mockMvc, CreateMenuItemRequest createMenuItemRequest) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post("/menuItems")
                .content(new ObjectMapper().writeValueAsString(createMenuItemRequest))
                .with(TestUserHelpers.kitchen()));
    }

    public static CreateMenuItemResponse mapCreateMenuItemResponse(ResultActions createMenuItemResultActions) throws UnsupportedEncodingException, JsonProcessingException {
        return new ObjectMapper().readValue(
                createMenuItemResultActions.andReturn().getResponse().getContentAsString(), CreateMenuItemResponse.class);
    }

    public static CreateMenuItemResponse createMenuItem(MockMvc mockMvc, CreateMenuItemRequest createMenuItemRequest) throws Exception {
        ResultActions resultActions = sendCreateMenuItemRequest(mockMvc, createMenuItemRequest);
        return mapCreateMenuItemResponse(resultActions);
    }

    public static ResultActions sendGetMenuItemRequest(MockMvc mockMvc, Long id) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.get("/menuItems/" + id));
    }

    public static GetMenuItemResponse mapGetMenuItemResponse(ResultActions getMenuItemResultActions) throws UnsupportedEncodingException, JsonProcessingException {
        return new ObjectMapper().readValue(
                getMenuItemResultActions.andReturn().getResponse().getContentAsString(), GetMenuItemResponse.class);
    }

    public static ResultActions sendGetMenuRequest(MockMvc mockMvc, Long kitchenId) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.get("/menuItems?kitchenId=" + kitchenId));
    }

    public static List<GetMenuItemResponse> mapGetMenuResponse(ResultActions getAllMenuItemsResultActions) throws UnsupportedEncodingException, JsonProcessingException {
        return new ObjectMapper().readValue(getAllMenuItemsResultActions.andReturn().getResponse().getContentAsString(),
                new TypeReference<List<GetMenuItemResponse>>() {
                });
    }

    public static ResultActions sendDeleteMenuItemRequest(MockMvc mockMvc, Long id) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.delete("/menuItems/" + id)
                .with(TestUserHelpers.kitchen()));
    }

}
