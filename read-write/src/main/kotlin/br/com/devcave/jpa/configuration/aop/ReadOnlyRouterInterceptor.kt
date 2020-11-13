package br.com.devcave.jpa.configuration.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Aspect
@Order(0)
@Component
@ConditionalOnProperty(
    name = ["transaction-routing.implementation"],
    havingValue = "aop",
    matchIfMissing = false
)
class ReadOnlyRouterInterceptor {
    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    fun proceed(pjp: ProceedingJoinPoint): Any? {
        try {
            if (isReadOnly(pjp)) {
                RoutingAOPDataSource.setReplicaRoute()
            }
            return pjp.proceed()
        } finally {
            RoutingAOPDataSource.clearReplicaRoute()
        }
    }

    fun isReadOnly(pjp: ProceedingJoinPoint): Boolean {
        val signature = pjp.signature as MethodSignature
        val method = signature.method

        return method.getAnnotation(Transactional::class.java)?.readOnly ?: false
    }
}