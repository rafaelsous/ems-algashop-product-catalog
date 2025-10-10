package contracts.product

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method POST()
        headers {
            accept "application/json"
            contentType "application/json"
        }
        urlPath("/api/v1/products") {
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
        status 201
        headers {
            contentType "application/json"
        }
        body([
                id          : anyUuid(),
                addedAt     : anyIso8601WithOffset(),
                name        : fromRequest().body('$.name'),
                brand       : fromRequest().body('$.brand'),
                regularPrice: fromRequest().body('$.regularPrice'),
                salePrice   : fromRequest().body('$.salePrice'),
                inStock     : false,
                enabled     : fromRequest().body('$.enabled'),
                description : fromRequest().body('$.description'),
                category    : [
                        id  : fromRequest().body('$.categoryId'),
                        name: "Informática"
                ]
        ])
    }
}