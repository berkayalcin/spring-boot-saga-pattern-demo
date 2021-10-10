package com.example.orderservice.infrastructure.config;

import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.springframework.context.annotation.Bean;

public class SerializerConfiguration {
    @Bean
    public Serializer buildSerializer() {
        return XStreamSerializer.builder().lenientDeserialization().build();
    }
}
