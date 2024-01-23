package api_gateway.routes

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import java.util.logging.Logger

class UsersServiceRoute(private val logger: Logger) {
    fun route(builder: RouteLocatorBuilder): RouteLocator =
        builder.routes().route("users_service_root_route") {
            it.path("/users/**").filters { f ->
                f.filter { exchange, chain ->
                    logger.info(" [Users Service] Request: ${exchange.request.uri}")
                    chain.filter(exchange)
                }
                f.rewritePath("/users/(?<segment>.*)", "/\$\\{segment}")
            }.uri("http://localhost:8888")
        }.build()
}