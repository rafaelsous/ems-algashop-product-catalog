package com.rafaelsousa.algashop.product.catalog.infrastructure.persistence.config;

import org.bson.UuidRepresentation;
import org.springframework.boot.mongodb.autoconfigure.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    @Bean
    public MongoClientSettingsBuilderCustomizer uuiCustomizer() {
        return builder -> builder.uuidRepresentation(UuidRepresentation.STANDARD);
    }
}