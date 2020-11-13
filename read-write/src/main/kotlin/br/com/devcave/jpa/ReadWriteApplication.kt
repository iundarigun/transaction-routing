package br.com.devcave.jpa

import br.com.devcave.jpa.configuration.DataSourceListProperties
import br.com.devcave.jpa.configuration.DataSourceProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableTransactionManagement
@EnableConfigurationProperties(value = [DataSourceListProperties::class])
class ReadWriteApplication

fun main(args: Array<String>) {
	runApplication<ReadWriteApplication>(*args)
}
