package contracts.product

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.MediaType

Contract.make {
    request {
        method PUT()
        headers {
            accept MediaType.APPLICATION_JSON_VALUE
            contentType MediaType.APPLICATION_JSON_VALUE
        }
        urlPath("/api/v1/products/0199c60b-0dce-7fee-9ef2-d6dc30a8e3fa") {
            body([
                    name        : value(
                            test("Notebook X11"),
                            stub(nonBlank())
                    ),
                    brand       : value(
                            test("Deep Diver"),
                            stub(nonBlank())
                    ),
                    regularPrice: value(
                            test(1500.00),
                            stub(number())
                    ),
                    salePrice   : value(
                            test(1000.00),
                            stub(number())
                    ),
                    description : value(
                            test("A Gamer Notebook"),
                            stub(optional(nonBlank()))
                    ),
                    enabled     : value(
                            test(true),
                            stub(anyBoolean())
                    ),
                    categoryId  : value(
                            test("0199c8e4-498f-7fed-8251-6a24c790fb38"),
                            stub(anyUuid())
                    )
            ])
        }
    }
    response {
        status 200
        headers {
            contentType MediaType.APPLICATION_JSON_VALUE
        }
        body([
                id          : fromRequest().path(3),
                createdAt   : anyIso8601WithOffset(),
                name        : fromRequest().body('$.name'),
                brand       : fromRequest().body('$.brand'),
                regularPrice: fromRequest().body('$.regularPrice'),
                salePrice   : fromRequest().body('$.salePrice'),
                inStock     : anyBoolean(),
                enabled     : fromRequest().body('$.enabled'),
                description : fromRequest().body('$.description'),
                category    : [
                        id  : anyUuid(),
                        name: "Informática"
                ]
        ])
    }
}