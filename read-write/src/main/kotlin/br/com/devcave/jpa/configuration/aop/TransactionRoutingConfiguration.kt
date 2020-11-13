package br.com.devcave.jpa.configuration.aop

import br.com.devcave.jpa.configuration.DataSourceListProperties
import br.com.devcave.jpa.configuration.DataSourceProperties
import br.com.devcave.jpa.configuration.DataSourceType
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import javax.sql.DataSource

@Configuration
@ConditionalOnProperty(
    name = ["transaction-routing.implementation"],
    havingValue = "aop",
    matchIfMissing = false
)
class TransactionRoutingConfiguration(
    private val dataSourceListProperties: DataSourceListProperties
) {
    @Bean
    @Primary
    fun dataSource(): DataSource {
        val transactionRoutingDataSource = RoutingAOPDataSource()
        val readDataSource = buildDataSourceFromProperties(dataSourceListProperties.readDatasource)
        val writeDataSource = buildDataSourceFromProperties(dataSourceListProperties.writeDatasource)
        val targetDataSources: MutableMap<Any, Any> = mutableMapOf(
            DataSourceType.READ_ONLY to readDataSource,
            DataSourceType.READ_WRITE to writeDataSource
        )

        transactionRoutingDataSource.setDefaultTargetDataSource(writeDataSource)

        transactionRoutingDataSource.setTargetDataSources(targetDataSources)
        transactionRoutingDataSource.afterPropertiesSet()
        return transactionRoutingDataSource
    }

    private fun buildDataSourceFromProperties(dataSourceProperty: DataSourceProperties): DataSource {
        return DataSourceBuilder.create()
                .url(dataSourceProperty.url)
                .username(dataSourceProperty.username)
                .password(dataSourceProperty.password)
                .driverClassName(dataSourceProperty.driverClassName)
                .build()
    }
}