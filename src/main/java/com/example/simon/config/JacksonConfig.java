package com.example.simon.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {

    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        // 配置LocalDateTime的序列化格式
        javaTimeModule.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));

        // 配置LocalDateTime的反序列化
        javaTimeModule.addDeserializer(LocalDateTime.class,
                new com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer(
                        DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));

        objectMapper.registerModule(javaTimeModule);
        return objectMapper;
    }
}