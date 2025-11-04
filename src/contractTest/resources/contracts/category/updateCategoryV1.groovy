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
        urlPath("/api/v1/categories/019a4c69-8aa0-7d88-9a45-001faf3599af") {
            body([
                    name   : value(
                            test("Computers"),
                            stub(nonBlank())
                    ),
                    enabled: value(
                            test(true),
                            stub(anyBoolean())
                    ),
            ])
        }
    }
    response {
        status 200
        headers {
            contentType MediaType.APPLICATION_JSON_VALUE
        }
        body([
                id     : fromRequest().path(3),
                name   : fromRequest().body('$.name'),
                enabled: fromRequest().body('$.enabled'),
        ])
    }
}