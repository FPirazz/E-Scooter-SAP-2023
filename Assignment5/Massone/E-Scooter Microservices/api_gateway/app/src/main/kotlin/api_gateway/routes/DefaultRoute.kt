package api_gateway.routes

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder

class DefaultRoute() {
    fun route(builder: RouteLocatorBuilder): RouteLocator = builder.routes().route("default_route") {
        it.path("/")
            // print when entering here
            .filters { f ->
                f.filter { exchange, chain ->
                    val request = exchange.request
                    // print the request url
                    println(" [Default Route] Request url ${request.uri}")
                    chain.filter(exchange)
                }
            }
            .uri("http://localhost:8081/")
    }.build()
}