package event.oms.adapter.out.persistence.support

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.hibernate.proxy.HibernateProxy
import java.io.Serializable

/**
 * JPA 엔티티를 위한 추상 기본 클래스.
 *
 * ID 필드와 프록시를 안전하게 처리하는 equals() 및 hashCode() 메서드를 제공합니다.
 * 이 클래스는 ID가 자동으로 생성(@GeneratedValue)되는 엔티티를 위한 것입니다.
 */
@MappedSuperclass
abstract class AbstractJpaEntity: Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    /**
     * JPA 프록시 객체와 실제 엔티티 객체를 동일하게 비교합니다.
     * ID가 null이 아닌(영속화된) 객체들만 ID를 기준으로 비교합니다.
     */
    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        // Hibernate 프록시인 경우 실제 엔티티 클래스를, 아니면 그냥 클래스를 가져옵니다.
        val oEffectiveClass = if (other is HibernateProxy) {
            other.hibernateLazyInitializer.persistentClass
        } else {
            other.javaClass
        }

        val thisEffectiveClass = if (this is HibernateProxy) {
            this.hibernateLazyInitializer.persistentClass
        } else {
            this.javaClass
        }
        // 클래스 타입이 다르면 다른 엔티티로 간주합니다.
        if (thisEffectiveClass != oEffectiveClass) return false

        other as AbstractJpaEntity // 클래스 타입이 같으므로 안전하게 캐스팅합니다.

        // ID가 null이 아니면(영속화됨) ID로 비교하고,
        // ID가 null이면(새 객체) 참조(===)로 비교합니다 (이미 맨 위에서 체크됨).
        return id != null && id == other.id
    }

    /**
     * JPA 프록시를 안전하게 처리하는 hashCode 구현입니다.
     * ID가 생성되기 전과 후에 일관된 해시코드를 반환하기 위해
     * (ID 대신) 클래스 자체의 해시코드를 사용합니다.
     */
    final override fun hashCode(): Int {
        val effectiveClass = if (this is HibernateProxy) {
            this.hibernateLazyInitializer.persistentClass
        } else {
            this.javaClass
        }
        return effectiveClass.hashCode()
    }

    override fun toString(): String {
        // Lombok @ToString과 유사하게, 프록시가 아닌 실제 클래스 이름과 ID를 출력합니다.
        val effectiveClass = if (this is HibernateProxy) {
            this.hibernateLazyInitializer.persistentClass
        } else {
            this.javaClass
        }
        return "${effectiveClass.simpleName}(id=$id)"
    }


}