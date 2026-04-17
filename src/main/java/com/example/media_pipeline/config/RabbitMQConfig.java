package com.example.media_pipeline.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "render_job_queue";
    public static final String EXCHANGE_NAME = "render_job_exchange";
    public static final String ROUTING_KEY = "render_job_routing_key";

    // 1. Define the Queue
    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, true); // true = durable (survives RabbitMQ restarts)
    }

    // 2. Define the Exchange
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    // 3. Bind the Queue to the Exchange
    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    // 4. Force Spring to serialize Java objects to JSON using the modernized converter
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}