package com.rafaelsousa.algashop.product.catalog.contract.base;

import com.rafaelsousa.algashop.product.catalog.application.category.management.CategoryInput;
import com.rafaelsousa.algashop.product.catalog.application.category.management.CategoryManagementApplicationService;
import com.rafaelsousa.algashop.product.catalog.application.product.query.PageModel;
import com.rafaelsousa.algashop.product.catalog.application.category.query.CategoryDetailOutput;
import com.rafaelsousa.algashop.product.catalog.application.category.query.CategoryDetailOutputTestDataBuilder;
import com.rafaelsousa.algashop.product.catalog.application.category.query.CategoryQueryService;
import com.rafaelsousa.algashop.product.catalog.presentation.CategoryController;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = CategoryController.class)
class CategoryBase {
    private static final UUID validCategoryId = UUID.fromString("0199c60b-0dce-7fee-9ef2-d6dc30a8e3fa");
    private static final UUID createdCategoryId = UUID.fromString("019a4c69-8aa0-7d88-9a45-001faf3599af");

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private CategoryQueryService productQueryService;

    @MockitoBean
    private CategoryManagementApplicationService productManagementApplicationService;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8).build());

        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();

        mockValidCategoryFindById();
        mockFilterCategories();
        mockCreateCategory();
    }

    private void mockValidCategoryFindById() {
        when(productQueryService.findById(validCategoryId))
                .thenReturn(CategoryDetailOutputTestDataBuilder.aCategory().id(validCategoryId).build());
    }

    private void mockFilterCategories() {
        when(productQueryService.filter(anyInt(), anyInt()))
                .then(answer -> {
                    Integer size = answer.getArgument(0);

                    return PageModel.<CategoryDetailOutput>builder()
                            .number(0)
                            .size(size)
                            .totalElements(2)
                            .content(
                                    List.of(
                                            CategoryDetailOutputTestDataBuilder.aCategory().build(),
                                            CategoryDetailOutputTestDataBuilder.aCategoryAlt().build()
                                    )
                            ).build();
                });
    }

    private void mockCreateCategory() {
        when(productManagementApplicationService.create(any(CategoryInput.class)))
                .thenReturn(createdCategoryId);

        when(productQueryService.findById(createdCategoryId))
                .thenReturn(CategoryDetailOutputTestDataBuilder.aCategory()
                        .id(createdCategoryId)
                        .name("Computers")
                        .enabled(true)
                        .build());
    }
}