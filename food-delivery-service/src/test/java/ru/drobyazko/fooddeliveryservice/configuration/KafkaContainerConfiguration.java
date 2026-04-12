package ru.drobyazko.fooddeliveryservice.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.kafka.KafkaContainer;


@TestConfiguration
public class KafkaContainerConfiguration {
    @Bean
    KafkaContainer kafkaContainer() {
        KafkaContainer kafkaContainer = new KafkaContainer("apache/kafka:4.1.2");
        kafkaContainer.start();
        return kafkaContainer;
    }

    @Bean
    DynamicPropertyRegistrar kafkaPropertyRegistrar(KafkaContainer kafkaContainer) {
        return registry -> {
            registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        };
    }
}

