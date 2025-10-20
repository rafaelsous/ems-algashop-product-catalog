package contracts.product

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.MediaType

Contract.make {
    request {
        method GET()
        headers {
            accept(MediaType.APPLICATION_JSON_VALUE)
        }
        url("/api/v1/products/019a0215-078c-7827-9bbe-e28d5402a5d4")
    }
    response {
        status NOT_FOUND()
        headers {
            contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
        }
        body([
                instance : fromRequest().path(),
                type     : "/errors/not-found",
                title    : "Not found",
                timestamp: anyIso8601WithOffset()
        ])
    }
}