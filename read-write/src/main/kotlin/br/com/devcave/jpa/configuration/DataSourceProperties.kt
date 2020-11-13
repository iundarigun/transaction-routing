package br.com.devcave.jpa.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "spring")
data class DataSourceListProperties(
    val writeDatasource: DataSourceProperties,
    val readDatasource: DataSourceProperties
)

@ConstructorBinding
data class DataSourceProperties(
    val url: String,
    val username: String,
    val password: String,
    val driverClassName: String
)