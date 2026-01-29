package ru.drobyazko.fooddeliveryservice;

import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import ru.drobyazko.fooddeliveryservice.security.infrastructure.Authority;
import ru.drobyazko.fooddeliveryservice.security.api.RegisterUserRequest;

import java.util.Set;

public class TestUserHelpers {
    private static final String NOOP_PREFIX = "{noop}";

    public static RequestPostProcessor user() {
        return SecurityMockMvcRequestPostProcessors.httpBasic(TestUsers.USER.getUsername(), TestUsers.USER.getPassword());
    }

    public static RequestPostProcessor kitchen() {
        return SecurityMockMvcRequestPostProcessors.httpBasic(TestUsers.KITCHEN.getUsername(), TestUsers.KITCHEN.getPassword());
    }

    public static RequestPostProcessor admin() {
        return SecurityMockMvcRequestPostProcessors.httpBasic(TestUsers.ADMIN.getUsername(), TestUsers.ADMIN.getPassword());
    }

    public static void RegisterDefaultUsers(MockMvc mockMvc) throws Exception {
        RegisterUserRequest registerUserRequest =
                new RegisterUserRequest(TestUsers.USER.getUsername(),
                        NOOP_PREFIX + TestUsers.USER.getPassword(), Set.of(Authority.USER));
        RegisterUserRequest registerKitchenRequest =
                new RegisterUserRequest(TestUsers.KITCHEN.getUsername(),
                        NOOP_PREFIX + TestUsers.KITCHEN.getPassword(), Set.of(Authority.KITCHEN));
        RegisterUserRequest registerAdminRequest =
                new RegisterUserRequest(TestUsers.ADMIN.getUsername(),
                        NOOP_PREFIX + TestUsers.ADMIN.getPassword(), Set.of(Authority.ADMIN));
        UserApiHelper.sendRegisterUserRequest(mockMvc, registerUserRequest);
        UserApiHelper.sendRegisterUserRequest(mockMvc, registerKitchenRequest);
        UserApiHelper.sendRegisterUserRequest(mockMvc, registerAdminRequest);
    }
}
