package com.rafaelsousa.algashop.product.catalog.infrastructure.persistence.config;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.bson.UuidRepresentation;
import org.springframework.boot.mongodb.autoconfigure.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

@Configuration
public class MongoConfig {

    @Bean
    public MongoClientSettingsBuilderCustomizer uuiCustomizer() {
        return builder -> builder.uuidRepresentation(UuidRepresentation.STANDARD);
    }

    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(
                List.of(
                        new OffsetDateTimeReadConverter(),
                        new OffsetDateTimeWriteConverter()
                )
        );
    }

	@Bean
    public MongoTransactionManager mongoTransactionManager(MongoDatabaseFactory factory) {
		return new MongoTransactionManager(factory);
	}

    public static class OffsetDateTimeReadConverter implements Converter<Date, OffsetDateTime> {

        @Override
        public OffsetDateTime convert(Date source) {
            return source.toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime();
        }
    }

    public static class OffsetDateTimeWriteConverter implements Converter<OffsetDateTime, Date> {

        @Override
        public Date convert(OffsetDateTime source) {
            return Date.from(source.toInstant());
        }
    }
}