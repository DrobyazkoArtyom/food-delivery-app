package ru.drobyazko.fooddeliveryservice.security.api;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.drobyazko.fooddeliveryservice.security.domain.aggregate.RegisterUser;
import ru.drobyazko.fooddeliveryservice.security.domain.aggregate.User;
import ru.drobyazko.fooddeliveryservice.security.application.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterUserResponse registerUser(@RequestBody @Valid RegisterUserRequest registerUserRequest) {
        RegisterUser registerUser = new RegisterUser(registerUserRequest.username(),
                registerUserRequest.password(),
                registerUserRequest.authorities());
        User user = userService.registerUser(registerUser);
        return new RegisterUserResponse(user.id(), user.username(), user.password(), user.authorities());
    }

    //TODO: this is for testing purposes and should probably be deleted
    @GetMapping
    public LoginUserResponse getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return new LoginUserResponse(userDetails.getUsername(), userDetails.getAuthorities());
    }
}
