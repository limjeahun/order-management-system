package event.oms.common.extensions

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 모든 클래스(Any)에서 `this.getLogger()`를 호출할 수 있도록 하는 확장 함수
 */
fun Any.getLogger(): Logger {
    return LoggerFactory.getLogger(this::class.java.enclosingClass ?: this::class.java)
}