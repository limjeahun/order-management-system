package event.oms.application.service.product

import event.oms.application.port.`in`.product.GetProductListQuery
import event.oms.application.port.out.product.LoadProductPort
import event.oms.domain.model.product.Product
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetProductListService(
    private val loadProductPort: LoadProductPort
): GetProductListQuery {
    /**
     * Outbound Port 를 통해 모든 상품 조회 요청
     */
    override fun getAllProducts(): List<Product> {
        return loadProductPort.findAll()
    }

}