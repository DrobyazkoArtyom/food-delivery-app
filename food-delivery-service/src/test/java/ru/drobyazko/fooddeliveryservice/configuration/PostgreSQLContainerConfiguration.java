package ru.drobyazko.fooddeliveryservice.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

@TestConfiguration
public class PostgreSQLContainerConfiguration {
    @Bean
    PostgreSQLContainer postgreSQLContainer() {
        PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:18.1-alpine");
        postgreSQLContainer.withCopyFileToContainer(
                MountableFile.forClasspathResource("postgresql.conf"), "/etc/postgresql/");
        postgreSQLContainer.addParameter("c", "config_file=/etc/postgresql/postgresql.conf");
        postgreSQLContainer.withCommand("-c config_file=/etc/postgresql/postgresql.conf");
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
