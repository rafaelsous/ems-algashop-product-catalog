package contracts.product

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

Contract.make {
    request {
        method DELETE()
        url("/api/v1/products/0199c60b-0dce-7fee-9ef2-d6dc30a8e3fa/enable")
    }
    response {
        status HttpStatus.NO_CONTENT.value()
    }
}