package com.rafaelsousa.algashop.product.catalog.contract.base;

import com.rafaelsousa.algashop.product.catalog.application.product.management.ProductInput;
import com.rafaelsousa.algashop.product.catalog.application.product.management.ProductManagementApplicationService;
import com.rafaelsousa.algashop.product.catalog.application.product.query.PageModel;
import com.rafaelsousa.algashop.product.catalog.application.product.query.ProductDetailOutput;
import com.rafaelsousa.algashop.product.catalog.application.product.query.ProductDetailOutputTestDataBuilder;
import com.rafaelsousa.algashop.product.catalog.application.product.query.ProductQueryService;
import com.rafaelsousa.algashop.product.catalog.presentation.ProductController;
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

@WebMvcTest(controllers = ProductController.class)
class ProductBase {
    private static final UUID validProductId = UUID.fromString("0199c60b-0dce-7fee-9ef2-d6dc30a8e3fa");
    private static final UUID createdProductId = UUID.fromString("0199f474-c1b6-7223-a073-bd4101cfa1c6");

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private ProductQueryService productQueryService;

    @MockitoBean
    private ProductManagementApplicationService productManagementApplicationService;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8).build());

        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();

        mockValidOrderFindById();
        mockFilterProducts();
        mockCreateProduct();
    }

    private void mockValidOrderFindById() {
        when(productQueryService.findById(validProductId))
                .thenReturn(ProductDetailOutputTestDataBuilder.aProduct().id(validProductId).build());
    }

    private void mockFilterProducts() {
        when(productQueryService.filter(anyInt(), anyInt()))
                .then(answer -> {
                    Integer size = answer.getArgument(0);

                    return PageModel.<ProductDetailOutput>builder()
                            .number(0)
                            .size(size)
                            .totalElements(2)
                            .content(
                                    List.of(
                                            ProductDetailOutputTestDataBuilder.aProduct().build(),
                                            ProductDetailOutputTestDataBuilder.aProductAlt().build()
                                    )
                            ).build();
                });
    }

    private void mockCreateProduct() {
        when(productManagementApplicationService.create(any(ProductInput.class)))
                .thenReturn(createdProductId);

        when(productQueryService.findById(createdProductId))
                .thenReturn(ProductDetailOutputTestDataBuilder.aProduct()
                        .id(createdProductId)
                        .inStock(false)
                        .build());
    }
}