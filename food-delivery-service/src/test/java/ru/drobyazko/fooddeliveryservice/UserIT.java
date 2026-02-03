package ru.drobyazko.fooddeliveryservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.drobyazko.fooddeliveryservice.security.api.LoginUserRequest;
import ru.drobyazko.fooddeliveryservice.security.api.LoginUserResponse;
import ru.drobyazko.fooddeliveryservice.security.api.RegisterUserRequest;
import ru.drobyazko.fooddeliveryservice.security.api.RegisterUserResponse;
import ru.drobyazko.fooddeliveryservice.security.infrastructure.Authority;

import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import({PostgreSQLContainerConfiguration.class, MockMvcConfiguration.class})
class UserIT {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql(scripts = "classpath:/TruncateAllTables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void givenIAmAUser_WhenITryToRegister() throws Exception {
        RegisterUserRequest registerUserRequest =
                new RegisterUserRequest("test", "{noop}test", Set.of(Authority.ADMIN));
        ResultActions registerUserResponseResultActions = UserApiHelper.sendRegisterUserRequest(mockMvc, registerUserRequest);
        registerUserResponseResultActions.andExpect(MockMvcResultMatchers.status().isCreated());
        RegisterUserResponse registerUserResponse = UserApiHelper.mapRegisterUserResponse(registerUserResponseResultActions);

        itShouldAllocateAnId(registerUserResponse);
        itShouldReturnSameUser(registerUserRequest, registerUserResponse);
    }

    private void itShouldAllocateAnId(RegisterUserResponse response) {
        Assertions.assertNotNull(response.id());
    }

    private void itShouldReturnSameUser(RegisterUserRequest request, RegisterUserResponse response) {
        Assertions.assertEquals(request.username(), response.username());
        Assertions.assertEquals(request.password(), response.password());
        Assertions.assertEquals(request.authorities(), response.authorities());
    }

    @Test
    @Sql(scripts = "classpath:/TruncateAllTables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void givenIAmAUser_WhenITryToLogin() throws Exception {
        RegisterUserRequest registerUserRequest = new RegisterUserRequest("test", "{noop}test", Set.of(Authority.ADMIN));
        ResultActions registerUserResponseResultActions = UserApiHelper.sendRegisterUserRequest(mockMvc, registerUserRequest);
        registerUserResponseResultActions.andExpect(MockMvcResultMatchers.status().isCreated());
        RegisterUserResponse registerUserResponse = UserApiHelper.mapRegisterUserResponse(registerUserResponseResultActions);

        itShouldAllocateAnId(registerUserResponse);
        itShouldReturnSameUser(registerUserRequest, registerUserResponse);

        LoginUserRequest loginUserRequest = new LoginUserRequest("test", "test");
        ResultActions loginUserResponseResultActions = UserApiHelper.sendLoginUserRequest(mockMvc, loginUserRequest);
        LoginUserResponse loginUserResponse = UserApiHelper.mapLoginUserResponse(loginUserResponseResultActions);

        itShouldReturnSameUsername(loginUserRequest, loginUserResponse);
    }

    private void itShouldReturnSameUsername(LoginUserRequest request, LoginUserResponse response) {
        Assertions.assertEquals(request.username(), response.username());
    }

}
