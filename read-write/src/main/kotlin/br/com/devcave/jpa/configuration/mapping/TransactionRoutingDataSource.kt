package br.com.devcave.jpa.configuration.mapping

import br.com.devcave.jpa.configuration.DataSourceType
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource
import org.springframework.transaction.support.TransactionSynchronizationManager

class TransactionRoutingDataSource : AbstractRoutingDataSource() {
    override fun determineCurrentLookupKey(): Any {
        return if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
            DataSourceType.READ_ONLY
        } else {
            DataSourceType.READ_WRITE
        }
    }
}