package api_gateway.routes

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpServerErrorException

class UsersServiceRoute(private val circuitBreaker: CircuitBreaker) {
    private val logger = LoggerFactory.getLogger(UsersServiceRoute::class.java)

    fun route(builder: RouteLocatorBuilder): RouteLocator = builder.routes()
        .route("users_service_root_route") {
            it.path("/users/**").filters { f ->
                f.filter { exchange, chain ->
                    try {
                        circuitBreaker.executeSupplier {
                            logger.info(" [Users Service] Request: ${exchange.request.uri}")
                            chain.filter(exchange)
                        }
                    } catch (e: HttpServerErrorException) {
                        logger.error("Circuit Breaker is open. Failed to handle incoming request.")
                        exchange.response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE)
                        return@filter exchange.response.setComplete()
                    }
                }
                f.rewritePath("/users/(?<segment>.*)", "/\$\\{segment}")
            }.uri("http://localhost:8888")
        }.build()
}