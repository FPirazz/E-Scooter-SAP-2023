package api_gateway.routes

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import java.util.logging.Logger

class RidesServiceRoute(private val logger: Logger) {
    fun route(builder: RouteLocatorBuilder): RouteLocator = builder.routes().route("rides_service_root_route") {
        it.path("/rides/").filters { f ->
            f.filter { exchange, chain ->
                val request = exchange.request
                // return exception if no email cookie
                request.cookies["email"]?.let {
                    logger.info(" [Rides Service] Request: ${request.uri}")
                } ?: throw Exception("No email cookie")
                chain.filter(exchange)
            }
            f.rewritePath("/rides/(?<segment>.*)", "/\$\\{segment}")
        }.uri("http://localhost:8081")
    }.build()
}