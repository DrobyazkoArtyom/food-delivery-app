package ru.drobyazko.fooddeliveryservice;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration
public class PostgreSQLContainerConfiguration {
    @Bean
    PostgreSQLContainer postgreSQLContainer() {
        PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:18.1-alpine");
        postgreSQLContainer.start();
        return postgreSQLContainer;
    }

    @Bean
    DynamicPropertyRegistrar postgreSQLPropertyRegistrar(PostgreSQLContainer postgreSQLContainer) {
        return registry -> {
            registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
            registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
            registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
            registry.add("spring.datasource.driver-class-name", postgreSQLContainer::getDriverClassName);
        };
    }
}
