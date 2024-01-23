package api_gateway.routes

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.logging.Logger

class RidesServiceRoute(private val logger: Logger) {
    fun route(builder: RouteLocatorBuilder): RouteLocator = builder.routes().route("rides_service_root_route") {
        it.path("/rides/**").filters { f ->
            f.filter { exchange, chain ->
                val request = exchange.request
                request.cookies["email"] ?: throw ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Unauthorized access"
                )

                logger.info(" [Rides Service] Request: ${exchange.request.uri}")
                chain.filter(exchange)
            }
            f.rewritePath("/rides/(?<segment>.*)", "/\$\\{segment}")
        }.uri("http://localhost:8081")
    }.build()
}