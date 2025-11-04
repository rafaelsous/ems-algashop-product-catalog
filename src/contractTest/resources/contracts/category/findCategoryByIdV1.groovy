package contracts.product

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method GET()
        headers {
            accept 'application/json'
        }
        url("/api/v1/categories/019a4c69-8aa0-7d88-9a45-001faf3599af")
    }
    response {
        status 200
        headers {
            contentType 'application/json'
        }
        body([
                id          : fromRequest().path(3),
                name        : "Computers",
        ])
    }
}