package ru.drobyazko.fooddeliveryservice.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.drobyazko.fooddeliveryservice.security.api.LoginUserRequest;
import ru.drobyazko.fooddeliveryservice.security.api.LoginUserResponse;
import ru.drobyazko.fooddeliveryservice.security.api.RegisterUserRequest;
import ru.drobyazko.fooddeliveryservice.security.api.RegisterUserResponse;

import java.io.UnsupportedEncodingException;

public class UserApiHelper {
    public static ResultActions sendRegisterUserRequest(MockMvc mockMvc, RegisterUserRequest registerUserRequest) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(new ObjectMapper().writeValueAsString(registerUserRequest)));
    }

    // TODO: instead of creating a new objectmapper for every method in every apihelper can create it once somewhere
    public static RegisterUserResponse mapRegisterUserResponse(ResultActions resultActions) throws UnsupportedEncodingException, JsonProcessingException {
        return new ObjectMapper().readValue(resultActions.andReturn().getResponse().getContentAsString(), RegisterUserResponse.class);
    }

    public static ResultActions sendLoginUserRequest(MockMvc mockMvc, LoginUserRequest loginUserRequest) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.get("/users")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(loginUserRequest.username(), loginUserRequest.password())));
    }

    public static LoginUserResponse mapLoginUserResponse(ResultActions resultActions) throws UnsupportedEncodingException, JsonProcessingException {
        return new ObjectMapper().readValue(resultActions.andReturn().getResponse().getContentAsString(), LoginUserResponse.class);
    }
}
