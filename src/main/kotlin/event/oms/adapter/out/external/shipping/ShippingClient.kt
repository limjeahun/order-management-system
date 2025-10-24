package event.oms.adapter.out.external.shipping

import event.oms.adapter.out.external.shipping.dto.request.ShippingRequest
import event.oms.adapter.out.external.shipping.dto.response.ShippingResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(name = "shippingClient", url = "\${external-api.shipping.url}")
interface ShippingClient {
    @PostMapping("/shipping/request")
    fun requestShipment(@RequestBody request: ShippingRequest): ShippingResponse
}