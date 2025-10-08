package contracts.product

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method GET()
        headers {
            accept 'application/json'
        }
        url("/api/v1/products/0199c60b-0dce-7fee-9ef2-d6dc30a8e3fa")
    }
    response {
        status 200
        headers {
            contentType 'application/json'
        }
        body([
                id: fromRequest().path(3),
                addedAt: anyIso8601WithOffset(),
                name: "Notebook X11",
                brand: "Deep Diver",
                regularPrice: 1500.00,
                salePrice: 1000.00,
                inStock: false,
                enabled: true,
                categoryId: anyUuid(),
                description: "A Gamer Notebook"
        ])
    }
}
