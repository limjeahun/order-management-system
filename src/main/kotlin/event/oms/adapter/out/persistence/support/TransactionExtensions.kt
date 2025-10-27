package event.oms.adapter.out.persistence.support

import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.TransactionTemplate

/**
 * TransactionTemplate 에서 PROPAGATION_REQUIRES_NEW 속성으로 블록을 실행하는 고차 확장 함수
 */
inline fun <T> TransactionTemplate.runInNewTransaction(crossinline block: () -> T): T? {
    val originalPropagation = this.propagationBehavior

    try {
        this.propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
        return this.execute {
            block()
        }
    } finally {
        this.propagationBehavior = originalPropagation
    }
}

/**
 * 반환 값이 없는 (Unit) 함수
 */
inline fun TransactionTemplate.runInNewTransactionWithoutResult(crossinline block: () -> Unit) {
    val originalPropagation = this.propagationBehavior
    try {
        this.propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
        this.executeWithoutResult {
            block()
        }
    } finally {
        this.propagationBehavior = originalPropagation
    }
}