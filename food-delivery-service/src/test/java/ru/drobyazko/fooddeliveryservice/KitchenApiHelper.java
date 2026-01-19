package ru.drobyazko.fooddeliveryservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.drobyazko.fooddeliveryservice.catalogue.api.CreateKitchenRequest;
import ru.drobyazko.fooddeliveryservice.catalogue.api.CreateKitchenResponse;
import ru.drobyazko.fooddeliveryservice.catalogue.api.GetKitchenResponse;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class KitchenApiHelper {
    public static ResultActions sendCreateKitchenRequest(MockMvc mockMvc, CreateKitchenRequest createKitchenRequest) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post("/kitchens")
                .content(new ObjectMapper().writeValueAsString(createKitchenRequest)));
    }

    public static CreateKitchenResponse mapCreateKitchenResponse(ResultActions resultActions) throws UnsupportedEncodingException, JsonProcessingException {
        return new ObjectMapper().readValue(resultActions.andReturn().getResponse().getContentAsString(), CreateKitchenResponse.class);
    }

    public static CreateKitchenResponse createKitchen(MockMvc mockMvc, CreateKitchenRequest createKitchenRequest) throws Exception {
        ResultActions resultActions = sendCreateKitchenRequest(mockMvc, createKitchenRequest);
        return mapCreateKitchenResponse(resultActions);
    }

    public static ResultActions sendGetKitchenRequest(MockMvc mockMvc, Long id) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.get("/kitchens/" + id));
    }

    public static GetKitchenResponse mapGetKitchenResponse(ResultActions resultActions) throws UnsupportedEncodingException, JsonProcessingException {
        return new ObjectMapper().readValue(resultActions.andReturn().getResponse().getContentAsString(), GetKitchenResponse.class);
    }

    public static ResultActions sendGetAllKitchensRequest(MockMvc mockMvc) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.get("/kitchens"));
    }

    public static List<GetKitchenResponse> mapGetAllKitchensResponse(ResultActions resultActions) throws UnsupportedEncodingException, JsonProcessingException {
        return new ObjectMapper().readValue(resultActions.andReturn().getResponse().getContentAsString(),
                new TypeReference<List<GetKitchenResponse>>() {
                });
    }

    public static ResultActions sendDeleteKitchenRequest(MockMvc mockMvc, Long id) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.delete("/kitchens/" + id));
    }
}
