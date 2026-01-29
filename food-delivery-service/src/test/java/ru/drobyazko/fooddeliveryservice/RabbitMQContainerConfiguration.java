package ru.drobyazko.fooddeliveryservice;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.RabbitMQContainer;

@TestConfiguration
public class RabbitMQContainerConfiguration {
    @Bean
    RabbitMQContainer rabbitMQContainer() {
        RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:4.2.3-alpine");
        rabbitMQContainer.start();
        return rabbitMQContainer;
    }

    @Bean
    DynamicPropertyRegistrar rabbitMqPropertyRegistrar(RabbitMQContainer rabbitMQContainer) {
        return registry -> {
            registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
            registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort);
            registry.add("spring.rabbitmq.username", rabbitMQContainer::getAdminUsername);
            registry.add("spring.rabbitmq.password", rabbitMQContainer::getAdminPassword);
        };
    }
}
