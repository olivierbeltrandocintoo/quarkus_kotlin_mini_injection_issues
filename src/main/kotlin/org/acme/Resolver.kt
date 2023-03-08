package org.acme

import io.quarkus.arc.Arc
import io.quarkus.arc.Priority
import org.jboss.resteasy.reactive.server.core.CurrentRequestManager
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.context.RequestScoped
import javax.enterprise.inject.Alternative
import javax.enterprise.inject.Default
import javax.ws.rs.core.UriInfo

interface Resolver {
    fun getInfo(): String
}

@ApplicationScoped
@Default
@Priority(1)
class ResolverNoRequest() : Resolver {
    override fun getInfo(): String {
        return "offlineInformation"
    }
}

@RequestScoped
@Alternative
@Priority(2)
class ResolverWithRequest(val uriInfo: UriInfo) : Resolver {
    override fun getInfo(): String {
        val hostname = uriInfo.baseUri.host
        return "onlineInformation with $hostname"
    }
}

@ApplicationScoped
class Consumer(val resolver: DispatchingResolver) {

    fun process(): String {
        return resolver.getInfo()
    }
}

//fun isRequestActive() = Arc.container().getActiveContext(RequestScoped::class.java) != null
fun isRequestActive() = CurrentRequestManager.get() != null


@ApplicationScoped
@Alternative
@Priority(10)
class DispatchingResolver(
    val global: ResolverNoRequest, val perRequest: ResolverWithRequest
) : Resolver {
    override fun getInfo(): String {
        return if (isRequestActive()) {
            perRequest.getInfo()
        } else {
            global.getInfo()
        }
    }
}