package ru.drobyazko.fooddeliveryservice;

import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import ru.drobyazko.fooddeliveryservice.security.Authority;

public class CustomSecurityMockMvcRequestPostProcessors {
    public static RequestPostProcessor user() {
        return SecurityMockMvcRequestPostProcessors.user("user").password("user").authorities(Authority.USER);
    }

    public static RequestPostProcessor kitchen() {
        return SecurityMockMvcRequestPostProcessors.user("kitchen").password("kitchen").authorities(Authority.KITCHEN);
    }

    public static RequestPostProcessor admin() {
        return SecurityMockMvcRequestPostProcessors.user("admin").password("admin").authorities(Authority.ADMIN);
    }
}
