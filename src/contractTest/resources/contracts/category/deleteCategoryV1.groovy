package contracts.product

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

Contract.make {
    request {
        method DELETE()
        headers {
            accept MediaType.APPLICATION_JSON_VALUE
        }
        url("/api/v1/categories/019a4c69-8aa0-7d88-9a45-001faf3599af")
    }
    response {
        status HttpStatus.NO_CONTENT.value()
    }
}