package api_gateway.routes

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder

class DefaultRoute() {
    fun route(builder: RouteLocatorBuilder): RouteLocator = builder.routes().route("default_route") {
        it.path("/**").uri("http://localhost:8081")
    }.build()
}