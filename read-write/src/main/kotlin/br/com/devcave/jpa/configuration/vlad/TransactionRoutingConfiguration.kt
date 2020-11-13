package br.com.devcave.jpa.configuration.vlad

import br.com.devcave.jpa.configuration.DataSourceListProperties
import br.com.devcave.jpa.configuration.DataSourceType
import br.com.devcave.jpa.configuration.TransactionRoutingDataSource
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.ttddyy.dsproxy.listener.logging.SLF4JQueryLoggingListener
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder
import org.hibernate.jpa.HibernatePersistenceProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.support.TransactionTemplate
import java.util.Properties
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

@Configuration
@EnableAspectJAutoProxy
@EnableTransactionManagement
@ConditionalOnProperty(
    name = ["transaction-routing.implementation"],
    havingValue = "vlad",
    matchIfMissing = false
)
class TransactionRoutingConfiguration(
    private val dataSourceListProperties: DataSourceListProperties
) {
    private val hibernateDialect = "org.hibernate.dialect.PostgreSQLDialect"
    private val DATA_SOURCE_PROXY_NAME = "DATA_SOURCE_PROXY"

    @Bean
    fun readWriteDataSource(): DataSource {
        return connectionPoolDataSource(DataSourceBuilder.create()
            .url(dataSourceListProperties.writeDatasource.url)
            .username(dataSourceListProperties.writeDatasource.username)
            .password(dataSourceListProperties.writeDatasource.password)
            .driverClassName(dataSourceListProperties.writeDatasource.driverClassName)
            .build())
    }

    @Bean
    fun readOnlyDataSource(): DataSource {
        return connectionPoolDataSource(DataSourceBuilder.create()
            .url(dataSourceListProperties.readDatasource.url)
            .username(dataSourceListProperties.readDatasource.username)
            .password(dataSourceListProperties.readDatasource.password)
            .driverClassName(dataSourceListProperties.readDatasource.driverClassName)
            .build())
    }

    @Bean
    fun actualDataSource(): TransactionRoutingDataSource {
        val routingDataSource = TransactionRoutingDataSource()
        val dataSourceMap: MutableMap<Any, Any?> = HashMap()
        dataSourceMap[DataSourceType.READ_WRITE] = readWriteDataSource()
        dataSourceMap[DataSourceType.READ_ONLY] = readOnlyDataSource()
        routingDataSource.setTargetDataSources(dataSourceMap)
        return routingDataSource
    }
    private fun packagesToScan(): Array<String> {
        return arrayOf(
            "br.com.devcave.jpa"
        )
    }
    /*

        private fun databaseType(): String {
            return Database.POSTGRESQL.name.toLowerCase()
        }
    */
    private fun hikariConfig(dataSource: DataSource?): HikariConfig? {
        val hikariConfig = HikariConfig()
        val cpuCores = Runtime.getRuntime().availableProcessors()
        hikariConfig.maximumPoolSize = cpuCores * 4
        hikariConfig.dataSource = dataSource
        hikariConfig.isAutoCommit = false
        return hikariConfig
    }

    private fun connectionPoolDataSource(dataSource: DataSource): HikariDataSource {
        return HikariDataSource(hikariConfig(dataSource))
    }
/*
    @Bean
    fun properties(): PropertySourcesPlaceholderConfigurer {
        return PropertySourcesPlaceholderConfigurer()
    }
*/

    private fun dataSource(): DataSource {
        val loggingListener = SLF4JQueryLoggingListener()
        return ProxyDataSourceBuilder
            .create(actualDataSource())
            .name(DATA_SOURCE_PROXY_NAME)
            .listener(loggingListener)
            .build()
    }

    @Bean
    fun entityManagerFactory(): LocalContainerEntityManagerFactoryBean? {
        val entityManagerFactoryBean = LocalContainerEntityManagerFactoryBean()
        entityManagerFactoryBean.persistenceUnitName = javaClass.simpleName
        entityManagerFactoryBean.persistenceProvider = HibernatePersistenceProvider()
        entityManagerFactoryBean.dataSource = dataSource()
        entityManagerFactoryBean.setPackagesToScan(*packagesToScan())
        val vendorAdapter = HibernateJpaVendorAdapter()
        val jpaDialect = vendorAdapter.jpaDialect
        jpaDialect.setPrepareConnection(false)
        entityManagerFactoryBean.jpaVendorAdapter = vendorAdapter
        entityManagerFactoryBean.setJpaProperties(additionalProperties())
        return entityManagerFactoryBean
    }

    @Bean
    fun transactionManager(entityManagerFactory: EntityManagerFactory): JpaTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = entityManagerFactory
        return transactionManager
    }

    @Bean
    fun transactionTemplate(entityManagerFactory: EntityManagerFactory): TransactionTemplate {
        return TransactionTemplate(transactionManager(entityManagerFactory))
    }

    private fun additionalProperties(): Properties {
        val properties = Properties()
        properties.setProperty(
            "hibernate.connection.provider_disables_autocommit",
            "true"
        )
        properties.setProperty("hibernate.dialect", hibernateDialect)
        properties.setProperty("hibernate.hbm2ddl.auto", "none")
        return properties
    }
}