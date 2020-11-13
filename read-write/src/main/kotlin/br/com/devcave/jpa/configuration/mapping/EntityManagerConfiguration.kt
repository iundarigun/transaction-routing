package br.com.devcave.jpa.configuration.mapping

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaDialect

@Configuration
@ConditionalOnProperty(
    name = ["transaction-routing.implementation"],
    havingValue = "mapping",
    matchIfMissing = false
)
class EntityManagerConfiguration(
    localContainerEntityManagerFactoryBean: LocalContainerEntityManagerFactoryBean
) {
    init {
        if (localContainerEntityManagerFactoryBean.jpaDialect is HibernateJpaDialect) {
            (localContainerEntityManagerFactoryBean.jpaDialect as HibernateJpaDialect)
                .setPrepareConnection(false)
        }
    }
}