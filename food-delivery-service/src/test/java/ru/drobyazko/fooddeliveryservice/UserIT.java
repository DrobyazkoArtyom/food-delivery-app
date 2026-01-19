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
import ru.drobyazko.fooddeliveryservice.security.*;

import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import({RestTestClientConfiguration.class, PostgreSQLContainerConfiguration.class})
class UserIT {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenIAmAUser_WhenITryToRegister() throws Exception {
        RegisterUserRequest registerUserRequest =
                new RegisterUserRequest("test", "{noop}test", Set.of(AuthorityType.ADMIN));
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
        Assertions.assertEquals(request.authorityTypes(), response.authorityTypes());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenIAmAUser_WhenITryToLogin() throws Exception {
        RegisterUserRequest registerUserRequest = new RegisterUserRequest("test", "{noop}test", Set.of(AuthorityType.ADMIN));
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
