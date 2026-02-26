package com.example.Config.KafkaConfig;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReactiveKafkaProperties {
    @Value("${kafka.bootstrap.servers}")
    String bootstrapServers;

    @Value("${payment.kafka.consumer-group-id}")
    String consumerGroupId;
}

