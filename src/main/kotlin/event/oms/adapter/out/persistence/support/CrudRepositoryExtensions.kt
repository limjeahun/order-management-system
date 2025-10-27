package event.oms.adapter.out.persistence.support

import jakarta.persistence.EntityNotFoundException
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.findByIdOrNull

/**
 * ID로 엔티티를 조회하되, 없으면 즉시 EntityNotFoundException 을 던집니다.
 *
 * @param T 엔티티 타입
 * @param ID 엔티티의 ID 타입
 * @param id 조회할 ID
 * @param message 예외 메시지 (람다로 지연 생성)
 */
inline fun <T : Any, ID : Any> CrudRepository<T, ID>.getOrThrow(
    id: ID,
    crossinline message: () -> String = { "entity 를 찾을 수 없습니다.(ID: $id)" }
): T {
    return this.findByIdOrNull(id)
        ?: throw EntityNotFoundException(message())
}

/**
 * ID로 JPA 엔티티를 조회하고, 성공 시 mapper 를 적용(도메인 객체)합니다.
 * 엔티티가 없으면 null 을 반환합니다.
 *
 * @param T 엔티티 타입
 * @param ID 엔티티의 ID 타입
 * @param R 변환될 도메인 객체 타입
 * @param id 조회할 ID
 * @param mapper 엔티티를 도메인 객체로 변환하는 함수
 */
inline fun <T : Any, ID : Any, R : Any> CrudRepository<T, ID>.findThenMap(
    id: ID,
    crossinline mapper: (T) -> R // "변환 로직"을 람다로 받음
): R? {
    return this.findByIdOrNull(id)?.let(mapper)
}