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
    public Queue orderPrepareQueue() {
        return new Queue("order-prepare-queue");
    }

    @Bean
    public Exchange orderExchange() {
        return new DirectExchange("order");
    }

    @Bean
    public Binding orderBinding(Queue orderPrepareQueue, Exchange orderExchange) {
        return BindingBuilder.bind(orderPrepareQueue).to(orderExchange).with("order.created").noargs();
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
