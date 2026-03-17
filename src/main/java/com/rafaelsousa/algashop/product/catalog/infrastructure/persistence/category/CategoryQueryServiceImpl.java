package com.rafaelsousa.algashop.product.catalog.infrastructure.persistence.category;

import com.rafaelsousa.algashop.product.catalog.application.category.query.CategoryDetailOutput;
import com.rafaelsousa.algashop.product.catalog.application.category.query.CategoryFilter;
import com.rafaelsousa.algashop.product.catalog.application.category.query.CategoryQueryService;
import com.rafaelsousa.algashop.product.catalog.application.PageModel;
import com.rafaelsousa.algashop.product.catalog.application.utility.Mapper;
import com.rafaelsousa.algashop.product.catalog.domain.model.category.Category;
import com.rafaelsousa.algashop.product.catalog.domain.model.category.CategoryNotFoundException;
import com.rafaelsousa.algashop.product.catalog.domain.model.category.CategoryRepository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryQueryServiceImpl implements CategoryQueryService {
    private final CategoryRepository categoryRepository;
    private final Mapper mapper;
    private final MongoOperations mongoOperations;

    @Override
    @Cacheable(cacheNames = "algashop:categories:v1", key = "#categoryId")
    public CategoryDetailOutput findById(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        return mapper.convert(category, CategoryDetailOutput.class);
    }

    @Override
    @Cacheable(cacheNames = "algashop:categories-filter:v1", key = "'default'", condition = "#filter.isCacheable()")
    public PageModel<CategoryDetailOutput> filter(CategoryFilter filter) {
        Query query = queryWith(filter);
        long totalItems = mongoOperations.count(query, Category.class);

        Sort sort = sortWith(filter);

        PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize(), sort);
        Query pageQuery = query.with(pageRequest);

        int totalPages = 0;
        List<Category> categories;

        if (totalItems > 0) {
            categories = mongoOperations.find(pageQuery, Category.class);
            totalPages = (int) Math.ceil((double) totalItems / pageRequest.getPageSize());
        } else {
            categories = new ArrayList<>();
        }

        List<CategoryDetailOutput> categoryDetailOutputs = categories.stream()
                .map(category -> mapper.convert(category, CategoryDetailOutput.class))
                .toList();

        return PageModel.<CategoryDetailOutput>builder()
                .content(categoryDetailOutputs)
                .size(pageRequest.getPageSize())
                .number(pageRequest.getPageNumber())
                .totalElements(totalItems)
                .totalPages(totalPages)
            .build();
    }

	@Override
	public OffsetDateTime lastModified() {
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.group().max("updatedAt").as("lastModified")
		);

		AggregationResults<Document> result = mongoOperations.aggregate(aggregation, "categories", Document.class);

		Document document = result.getUniqueMappedResult();

		if (document == null) {
			return OffsetDateTime.now();
		}

        return document.getDate("lastModified").toInstant().atOffset(ZoneOffset.UTC);
	}

	private Query queryWith(CategoryFilter filter) {
        Query query = new Query();

        filterByName(filter.getName(), query);
        filterByEnable(filter.getEnabled(), query);

        return query;
    }

    private Sort sortWith(CategoryFilter filter) {
        return Sort.by(filter.getSortDirectionOrDefault(),
                filter.getSortByPropertyOrDefault().getPropertyName());
    }

    private static void filterByName(String name, Query query) {
        if ( StringUtils.isNotBlank(name)) {
            query.addCriteria(Criteria.where("name").regex(name.trim(), "i"));
        }
    }

    private static void filterByEnable(Boolean isEnabled, Query query) {
        if (isEnabled != null) {
            query.addCriteria(Criteria.where("enabled").is(isEnabled));
        }
    }
}