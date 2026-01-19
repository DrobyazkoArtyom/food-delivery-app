package ru.drobyazko.fooddeliveryservice;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.MockMvcBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@TestConfiguration
public class MockMvcConfiguration {
    @Bean
    public MockMvcBuilderCustomizer mockMvcBuilderCustomizer() {
        return builder -> {
            builder.defaultRequest(MockMvcRequestBuilders.get("/")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .build();
        };
    }
}
