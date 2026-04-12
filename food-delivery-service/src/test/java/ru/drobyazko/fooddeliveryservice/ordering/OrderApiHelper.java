package ru.drobyazko.fooddeliveryservice.ordering;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.drobyazko.fooddeliveryservice.security.TestUserHelpers;
import ru.drobyazko.fooddeliveryservice.ordering.api.GetOrderResponse;
import ru.drobyazko.fooddeliveryservice.ordering.api.PlaceOrderRequest;
import ru.drobyazko.fooddeliveryservice.ordering.api.PlaceOrderResponse;

import java.io.UnsupportedEncodingException;

public class OrderApiHelper {
    public static ResultActions sendPlaceOrderRequest(MockMvc mockMvc, PlaceOrderRequest placeOrderRequest) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post("/orders")
                .content(new ObjectMapper().writeValueAsString(placeOrderRequest))
                .with(TestUserHelpers.user()));
    }

    public static PlaceOrderResponse mapPlaceOrderResponse(ResultActions placeOrderResultActions) throws UnsupportedEncodingException, JsonProcessingException {
        return new ObjectMapper().readValue(
                placeOrderResultActions.andReturn().getResponse().getContentAsString(), PlaceOrderResponse.class);
    }

    public static ResultActions sendGetOrderRequest(MockMvc mockMvc, Long id) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.get("/orders/" + id)
                .with(TestUserHelpers.user()));
    }

    public static GetOrderResponse mapGetOrderResponse(ResultActions getOrderResultActions) throws UnsupportedEncodingException, JsonProcessingException {
        return new ObjectMapper().readValue(
                getOrderResultActions.andReturn().getResponse().getContentAsString(), GetOrderResponse.class);
    }
}
