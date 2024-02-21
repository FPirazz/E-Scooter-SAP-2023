package api_gateway.routes

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.util.StreamUtils
import org.springframework.web.client.HttpServerErrorException
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets

class RidesServiceRoute(private val circuitBreaker: CircuitBreaker) {
    private val logger = LoggerFactory.getLogger(RidesServiceRoute::class.java)

    fun route(builder: RouteLocatorBuilder): RouteLocator = builder.routes()
        .route("rides_service_root_route") {
            it.path("/rides/**").filters { f ->
                f.filter { exchange, chain ->
                    try {
                        circuitBreaker.executeSupplier {
                            val request = exchange.request
                            // Check if email cookie is set
                            val emailCookie = request.cookies["email"]
                            emailCookie?.let {
                                logger.info(" [Rides Service] Email cookie is set. Request: ${request.uri}")
                            } ?: run {
                                logger.info(" [Rides Service] Email cookie is not set. Redirecting to login page.")
                                val redirectHtml = ClassPathResource("redirect.html")
                                val htmlContent =
                                    StreamUtils.copyToString(redirectHtml.inputStream, StandardCharsets.UTF_8)
                                exchange.response.headers.contentType = MediaType.TEXT_HTML
                                return@executeSupplier exchange.response.writeWith(
                                    Mono.just(
                                        exchange.response.bufferFactory().wrap(htmlContent.toByteArray())
                                    )
                                )
                            }
                            chain.filter(exchange)
                        }
                    } catch (e: HttpServerErrorException) {
                        logger.error("Circuit Breaker is open. Failed to handle incoming request.")
                        exchange.response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE)
                        return@filter exchange.response.setComplete()
                    }
                }
                f.rewritePath("/rides/(?<segment>.*)", "/\$\\{segment}")
            }.uri("http://localhost:8081")
        }.build()
}