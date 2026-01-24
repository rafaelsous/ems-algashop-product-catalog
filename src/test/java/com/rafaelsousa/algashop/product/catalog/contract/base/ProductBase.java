package com.rafaelsousa.algashop.product.catalog.contract.base;

import com.rafaelsousa.algashop.product.catalog.application.product.ResourceNotFoundException;
import com.rafaelsousa.algashop.product.catalog.application.product.management.ProductInput;
import com.rafaelsousa.algashop.product.catalog.application.product.management.ProductManagementApplicationService;
import com.rafaelsousa.algashop.product.catalog.application.product.query.PageModel;
import com.rafaelsousa.algashop.product.catalog.application.product.query.ProductDetailOutput;
import com.rafaelsousa.algashop.product.catalog.application.product.query.ProductDetailOutputTestDataBuilder;
import com.rafaelsousa.algashop.product.catalog.application.product.query.ProductQueryService;
import com.rafaelsousa.algashop.product.catalog.presentation.product.ProductController;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.templates.TemplateFormats;
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
@ExtendWith(RestDocumentationExtension.class)
class ProductBase {
    private static final UUID validProductId = UUID.fromString("0199c60b-0dce-7fee-9ef2-d6dc30a8e3fa");
    private static final UUID createdProductId = UUID.fromString("0199f474-c1b6-7223-a073-bd4101cfa1c6");
    private static final UUID invalidProductId = UUID.fromString("019a0215-078c-7827-9bbe-e28d5402a5d4");

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private ProductQueryService productQueryService;

    @MockitoBean
    private ProductManagementApplicationService productManagementApplicationService;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentationContextProvider) {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentationContextProvider)
                        .snippets().withTemplateFormat(TemplateFormats.asciidoctor())
                        .and().operationPreprocessors()
                        .withResponseDefaults(Preprocessors.prettyPrint()))
                        .alwaysDo(MockMvcRestDocumentation.document("{ClassName}/{methodName}"))
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8).build());

        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();

        mockValidProductFindById();
        mockFilterProducts();
        mockCreateProduct();
        mockInvalidProductFindById();
    }

    private void mockValidProductFindById() {
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

    private void mockInvalidProductFindById() {
        when(productQueryService.findById(invalidProductId))
                .thenThrow(new ResourceNotFoundException());
    }
}