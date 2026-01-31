package ru.drobyazko.fooddeliveryservice;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.amqp.autoconfigure.RabbitTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfiguration {
    @Bean
    public Exchange orderExchange() {
        return new TopicExchange("order");
    }

    @Bean
    public Declarables queues() {
        return new Declarables(
                new Queue("order-prepare-queue"),
                new Queue("order-finish-queue")
        );
    }

    @Bean
    public Declarables bindings() {
        return new Declarables(
                new Binding("order-prepare-queue", Binding.DestinationType.QUEUE, "order", "order.created.#", null),
                new Binding("order-finish-queue", Binding.DestinationType.QUEUE, "order", "order.prepared", null)
        );
    }

    @Bean
    public MessageConverter jacksonJsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplateCustomizer rabbitTemplateCustomizer(MessageConverter jacksonJsonMessageConverter) {
        return rabbitTemplate -> {
            rabbitTemplate.setExchange("order");
            rabbitTemplate.setMessageConverter(jacksonJsonMessageConverter);
            rabbitTemplate.setChannelTransacted(true);
        };
    }
}
