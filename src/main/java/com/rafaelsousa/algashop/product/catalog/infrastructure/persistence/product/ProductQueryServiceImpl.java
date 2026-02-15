package com.rafaelsousa.algashop.product.catalog.infrastructure.persistence.product;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

import com.rafaelsousa.algashop.product.catalog.application.PageModel;
import com.rafaelsousa.algashop.product.catalog.application.product.query.*;
import com.rafaelsousa.algashop.product.catalog.application.utility.Mapper;
import com.rafaelsousa.algashop.product.catalog.domain.model.product.Product;
import com.rafaelsousa.algashop.product.catalog.domain.model.product.ProductNotFoundException;
import com.rafaelsousa.algashop.product.catalog.domain.model.product.ProductRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductQueryServiceImpl implements ProductQueryService {
    public static final String SCORE_PROPERTY_NAME = "score";
    public static final String ENABLED_PROPERTY_NAME = "enabled";
    public static final String CREATED_AT_PROPERTY_NAME = "createdAt";
    public static final String SALE_PRICE_PROPERTY_NAME = "salePrice";
    public static final String QUANTITY_IN_STOCK_PROPERTY_NAME = "quantityInStock";

    private final ProductRepository productRepository;
    private final Mapper mapper;
    private final MongoOperations mongoOperations;

    @Override
    public ProductDetailOutput findById(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        return mapper.convert(product, ProductDetailOutput.class);
    }

    @Override
    public PageModel<ProductSummaryOutput> filter(ProductFilter filter) {
        Optional<Criteria> criteria = buildCriteria(filter);
        Optional<TextCriteria> textCriteria = buildTextCriteria(filter.getTerm());

        Query query = new Query();
        textCriteria.ifPresent(query::addCriteria);
        criteria.ifPresent(query::addCriteria);

        long totalElements = mongoOperations.count(query, Product.class);

        if (totalElements == 0L) {
            return PageModel.<ProductSummaryOutput>builder()
                    .size(0)
                    .number(0)
                    .totalPages(0)
                    .totalElements(0)
                .build();
        }

        List<AggregationOperation> operations = new ArrayList<>();

    textCriteria.ifPresent(
        tc -> {
          operations.add(Aggregation.match(tc));
          AggregationOperation addTextScoreField = _ -> new Document(
                  "$addFields", new Document(SCORE_PROPERTY_NAME, new Document("$meta", "textScore")));
          operations.add(addTextScoreField);
        });
        criteria.ifPresent(c -> operations.add(Aggregation.match(c)));

        PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());

        operations.addAll(Arrays.asList(
                sort(sortWith(filter)),
                projectionForSummary(),
                skip(pageRequest.getOffset()),
                limit(filter.getSize())
        ));

        Aggregation aggregation = Aggregation.newAggregation(operations);

        List<ProductSummaryOutput> productSummaryOutputs = mongoOperations
                .aggregate(aggregation, Product.class, ProductSummaryOutput.class)
                .getMappedResults();

        int totalPages = (int) Math.ceil((double) totalElements / (double) filter.getSize());

        return PageModel.<ProductSummaryOutput>builder()
                .content(productSummaryOutputs)
                .number(filter.getPage())
                .size(filter.getSize())
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
    }

    private Optional<Criteria> buildCriteria(ProductFilter filter) {
        List<CriteriaDefinition> criterias = new ArrayList<>();

        filterByEnabled(filter.getEnabled(), criterias);
        filterByCreationDateRange(filter.getCreatedAtFrom(), filter.getCreatedAtTo(), criterias);
        filterByPriceRange(filter.getPriceFrom(), filter.getPriceTo(), criterias);
        filterByHasDiscount(filter.getHasDiscount(), criterias);
        filterByInStock(filter.getInStock(), criterias);
        filterByCategoriesId(filter.getCategoriesId(), criterias);

        if (criterias.isEmpty()) return Optional.empty();

        return Optional.of(new Criteria().andOperator(criterias.toArray(new Criteria[0])));
    }

    public Optional<TextCriteria> buildTextCriteria(String term) {
        if (StringUtils.isNotBlank(term)) {
            return Optional.of(TextCriteria.forDefaultLanguage().matching(term));
        }

        return Optional.empty();
    }

    private Sort sortWith(ProductFilter filter) {
        if (StringUtils.isNotBlank(filter.getTerm())) {
            return Sort.by(SCORE_PROPERTY_NAME);
        }

        return Sort.by(filter.getSortDirectionOrDefault(), filter.getSortByPropertyOrDefault().getPropertyName());
    }

    private ProjectionOperation projectionForSummary() {
        return project(ProductSummaryOutput.class)
                .andExpression("salePrice < regularPrice").as("hasDiscount")
                .andExpression("quantityInStock > 0").as("inStock")
                .and(StringOperators.Substr.valueOf("description")
                        .substring(0, 50)).as("shortDescription");
    }

    private static void filterByEnabled(Boolean isEnabled, List<CriteriaDefinition> criterias) {
        if (isEnabled != null) {
            criterias.add(Criteria.where(ENABLED_PROPERTY_NAME).is(isEnabled));
        }
    }

    private static void filterByCreationDateRange(OffsetDateTime createdAtFrom, OffsetDateTime createdAtTo, List<CriteriaDefinition> criterias) {
        if (createdAtFrom != null && createdAtTo != null) {
            criterias.add(Criteria.where(CREATED_AT_PROPERTY_NAME)
                    .gte(createdAtFrom)
                    .lte(createdAtTo)
            );
        } else {
            if (createdAtFrom != null) {
                criterias.add(Criteria.where(CREATED_AT_PROPERTY_NAME).gte(createdAtFrom));
            } else if (createdAtTo != null) {
                criterias.add(Criteria.where(CREATED_AT_PROPERTY_NAME).lte(createdAtTo));
            }
        }
    }

    private static void filterByPriceRange(BigDecimal priceFrom, BigDecimal priceTo, List<CriteriaDefinition> criterias) {
        if (priceFrom != null && priceTo != null) {
            criterias.add(Criteria.where(SALE_PRICE_PROPERTY_NAME)
                    .gte(priceFrom)
                    .lte(priceTo)
            );
        } else {
            if (priceFrom != null) {
                criterias.add(Criteria.where(SALE_PRICE_PROPERTY_NAME).gte(priceFrom));
            } else if (priceTo != null) {
                criterias.add(Criteria.where(SALE_PRICE_PROPERTY_NAME).lte(priceTo));
            }
        }
    }

    private static void filterByHasDiscount(Boolean hasDiscount, List<CriteriaDefinition> criterias) {
        if (hasDiscount != null) {
            if (hasDiscount) {
                criterias.add(AggregationExpressionCriteria.whereExpr(
                        ComparisonOperators.valueOf("$salePrice")
                                .lessThan("$regularPrice")
                ));
            } else {
                criterias.add(AggregationExpressionCriteria.whereExpr(
                        ComparisonOperators.valueOf("$salePrice")
                                .equalTo("$regularPrice")
                ));
            }
        }
    }

    private static void filterByInStock(Boolean inStock, List<CriteriaDefinition> criterias) {
        if (inStock != null) {
            if (inStock) {
                criterias.add(Criteria.where(QUANTITY_IN_STOCK_PROPERTY_NAME).gt(0));
            } else {
                criterias.add(Criteria.where(QUANTITY_IN_STOCK_PROPERTY_NAME).is(0));
            }
        }
    }

    private static void filterByCategoriesId(UUID[] categoriesId, List<CriteriaDefinition> criterias) {
        if (categoriesId != null && categoriesId.length > 0) {
            criterias.add(Criteria.where("category.id").in((Object[]) categoriesId));
        }
    }
}