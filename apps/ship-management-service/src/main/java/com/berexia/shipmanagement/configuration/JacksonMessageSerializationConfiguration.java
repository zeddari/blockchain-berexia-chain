package com.berexia.shipmanagement.configuration;

import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonMessageSerializationConfiguration {

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();

        // Allow specific package(s) for deserialization
        javaTypeMapper.setTrustedPackages("com.berexia.tradechain.dtos");
        converter.setJavaTypeMapper(javaTypeMapper);

        return converter;
    }
}
