package com.rafaelsousa.algashop.product.catalog.infrastructure.persistence.dataload;

import com.rafaelsousa.algashop.product.catalog.infrastructure.utility.AlgaShopResourceUtils;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.Document;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {
    private final MongoOperations mongoOperations;
    private final DataLoadProperties properties;

    @Override
    public void run(@NonNull ApplicationArguments args) {
        if (!properties.isEnabled()) return;

        log.info("Data load started");
        if ( CollectionUtils.isEmpty(properties.getSources())) {
            log.info("No sources configured");
            return;
        }

        properties.getSources()
                .forEach(this::importJsonFileToCollection);
  }

    private void importJsonFileToCollection(DataLoadProperties.DataLoadSource source) {
        String rawJson = AlgaShopResourceUtils.readContent(source.getLocation());

        if ( StringUtils.isBlank(rawJson)) {
            log.info("Resource {} is empty or not found", source.getLocation());
            return;
        }

        List<Document> documents = parseJsonToDocument(rawJson);
        int insertedTotal = insertInto(documents, source.getCollection());
        log.info("{} - Imports: {}/{}", source.getCollection(), insertedTotal, documents.size());
    }

    private List<Document> parseJsonToDocument(String rawJson) {
        try {
            BsonArray array = BsonArray.parse(rawJson);

            return array.stream()
                    .map(Object::toString)
                    .map(Document::parse)
                    .toList();
        } catch (Exception ex) {
            log.error("Error parsing JSON resource: {}", ex.getMessage(), ex);
            return Collections.emptyList();
        }
    }

    private int insertInto(List<Document> mongoDocs, String collectionName) {
        if (CollectionUtils.isEmpty(mongoDocs)) return 0;

        try {
            if (properties.isAutoDelete()) {
                mongoOperations.getCollection(collectionName).deleteMany(new BsonDocument());
            }

            return mongoOperations.insert(mongoDocs, collectionName).size();
        } catch (Exception ex) {
            log.error("Error inserting documents into {}: {}", collectionName, ex.getMessage(), ex);
        }

        return 0;
    }
}