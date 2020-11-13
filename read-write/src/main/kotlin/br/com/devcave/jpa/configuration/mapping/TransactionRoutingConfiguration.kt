package br.com.devcave.jpa.configuration.mapping

import br.com.devcave.jpa.configuration.DataSourceListProperties
import br.com.devcave.jpa.configuration.DataSourceProperties
import br.com.devcave.jpa.configuration.DataSourceType
import br.com.devcave.jpa.configuration.TransactionRoutingDataSource
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource
import javax.sql.DataSource

@Configuration
@ConditionalOnProperty(
    name = ["transaction-routing.implementation"],
    havingValue = "mapping",
    matchIfMissing = false
)
class TransactionRoutingConfiguration(
    private val dataSourceListProperties: DataSourceListProperties
) {

    @Bean
    @Primary
    fun dataSource(): DataSource {
        val transactionRoutingDataSource: AbstractRoutingDataSource = TransactionRoutingDataSource()
        val readDataSource = buildDataSourceFromProperties(dataSourceListProperties.readDatasource, true)
        val writeDataSource = buildDataSourceFromProperties(dataSourceListProperties.writeDatasource, false)
        val targetDataSources: MutableMap<Any, Any> = mutableMapOf(
            DataSourceType.READ_ONLY to readDataSource,
            DataSourceType.READ_WRITE to writeDataSource
        )

        transactionRoutingDataSource.setDefaultTargetDataSource(readDataSource)

        transactionRoutingDataSource.setTargetDataSources(targetDataSources)
        transactionRoutingDataSource.afterPropertiesSet()
        return transactionRoutingDataSource
    }

    private fun buildDataSourceFromProperties(dataSourceProperty: DataSourceProperties, readOnly: Boolean): DataSource {
        return connectionPoolDataSource(
            DataSourceBuilder.create()
                .url(dataSourceProperty.url)
                .username(dataSourceProperty.username)
                .password(dataSourceProperty.password)
                .driverClassName(dataSourceProperty.driverClassName)
                .build(),
            readOnly
        )
    }

    private fun hikariConfig(dataSource: DataSource, readOnly: Boolean): HikariConfig {
        val hikariConfig = HikariConfig()
        hikariConfig.dataSource = dataSource
        hikariConfig.isAutoCommit = false
        hikariConfig.isReadOnly = readOnly
        return hikariConfig
    }

    private fun connectionPoolDataSource(dataSource: DataSource, readOnly: Boolean): HikariDataSource {
        return HikariDataSource(hikariConfig(dataSource, readOnly))
    }
}