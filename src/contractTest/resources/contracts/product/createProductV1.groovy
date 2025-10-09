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
                    name        : "Notebook X11",
                    brand       : "Deep Diver",
                    regularPrice: 1500.00,
                    salePrice   : 1000.00,
                    description : "A Gamer Notebook",
                    enabled     : true,
                    categoryId  : "0199c8e4-498f-7fed-8251-6a24c790fb38"
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
                name        : "Notebook X11",
                brand       : "Deep Diver",
                regularPrice: 1500.00,
                salePrice   : 1000.00,
                inStock     : false,
                enabled     : true,
                description : "A Gamer Notebook",
                category    : [
                        id  : "0199c8e4-498f-7fed-8251-6a24c790fb38",
                        name: "Informática"
                ]
        ])
    }
}