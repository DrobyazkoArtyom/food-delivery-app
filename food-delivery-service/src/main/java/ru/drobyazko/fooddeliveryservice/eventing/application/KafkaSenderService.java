package ru.drobyazko.fooddeliveryservice.eventing.application;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ru.drobyazko.fooddeliveryservice.eventing.infrastructure.EventEntity;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaSenderService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaSenderService(@Qualifier("kafkaTemplate") KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public CompletableFuture<SendResult<String, String>> send(EventEntity eventEntity) {
        return kafkaTemplate.send(eventEntity.getType(), eventEntity.getPayload());
    }

    public void send(String topic, String payload) {
        kafkaTemplate.send(topic, payload);
    }
}
