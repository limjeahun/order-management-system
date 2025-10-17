package event.oms.application.service.product

import event.oms.application.port.`in`.product.AddProductCommand
import event.oms.application.port.`in`.product.AddProductUseCase
import event.oms.application.port.out.product.SaveProductPort
import event.oms.domain.model.product.Product
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AddProductService(
    private val saveProductPort: SaveProductPort,
) : AddProductUseCase {
    override fun addProduct(command: AddProductCommand): Product {
        val newProduct = Product(
            id    = command.id,
            name  = command.name,
            price = command.price,
            stock = command.stock,
        )
        return saveProductPort.save(newProduct)
    }
}