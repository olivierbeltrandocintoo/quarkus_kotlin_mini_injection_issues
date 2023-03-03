package org.acme

import io.quarkus.arc.Arc
import io.quarkus.arc.Priority
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

fun isRequestActive() = Arc.container().getActiveContext(RequestScoped::class.java) != null


@ApplicationScoped
@Alternative
@Priority(10)
class DispatchingResolver(
    val global: ResolverNoRequest,
    val perRequest: ResolverWithRequest
) : Resolver {
    override fun getInfo(): String {
        if (isRequestActive()) {
            return perRequest.getInfo()
        } else {
            return global.getInfo()
        }
    }
}