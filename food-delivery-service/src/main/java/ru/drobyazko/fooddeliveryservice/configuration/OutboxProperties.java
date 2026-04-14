package ru.drobyazko.fooddeliveryservice.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.outbox")
public record OutboxProperties(int batchSize, int processingDelaySeconds) {
}
