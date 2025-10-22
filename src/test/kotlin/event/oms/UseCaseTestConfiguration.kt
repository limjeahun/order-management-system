package event.oms

import event.oms.application.port.`in`.product.AddProductUseCase
import event.oms.application.port.`in`.product.UpdateProductUseCase
import event.oms.application.service.product.GetProductListService
import org.mockito.Mockito
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
internal class UseCaseTestConfiguration {
    @Bean
    fun addProductUseCase(): AddProductUseCase {
        // Mockito.mock()을 사용하여 Mock 객체 생성 및 반환
        return Mockito.mock(AddProductUseCase::class.java)
    }

    @Bean
    fun updateProductUseCase(): UpdateProductUseCase {
        return Mockito.mock(UpdateProductUseCase::class.java)
    }

    @Bean
    fun getProductListService() : GetProductListService {
        return Mockito.mock(GetProductListService::class.java)
    }


}