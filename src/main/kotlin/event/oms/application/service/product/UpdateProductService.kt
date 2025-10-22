package event.oms.application.service.product

import event.oms.application.port.`in`.product.UpdateProductCommand
import event.oms.application.port.`in`.product.UpdateProductUseCase
import event.oms.application.port.out.product.LoadProductPort
import event.oms.application.port.out.product.SaveProductPort
import event.oms.domain.model.product.Product
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UpdateProductService(
    private val loadProductPort: LoadProductPort,
    private val saveProductPort: SaveProductPort,
): UpdateProductUseCase {
    override fun updateProduct(command: UpdateProductCommand): Product {
        // 수정할 제품 조회
        val product = loadProductPort.findById(command.id)
            ?: throw NoSuchElementException("ID가 ${command.id}인 제품을 찾을 수 없습니다.")
        // 제품 수정 데이터 적용
        product.updateDetails(
            command.name,
            command.price,
            command.stock,
        )
        // 3. Outbound Port를 통해 변경된 제품 정보 저장
        return saveProductPort.save(product)
    }
}