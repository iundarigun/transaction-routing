package br.com.devcave.jpa.configuration.aop

import br.com.devcave.jpa.configuration.DataSourceType
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource

class RoutingAOPDataSource : AbstractRoutingDataSource() {
    companion object {
        val ctx = ThreadLocal<DataSourceType>()

        fun clearReplicaRoute() {
            ctx.remove()
        }

        fun setReplicaRoute() {
            ctx.set(DataSourceType.READ_ONLY)
        }
    }

    override fun determineCurrentLookupKey(): Any? {
        return ctx.get()
    }
}
