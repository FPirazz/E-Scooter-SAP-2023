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

class DefaultRoute(private val circuitBreaker: CircuitBreaker) {
    private val logger = LoggerFactory.getLogger(DefaultRoute::class.java)
    fun route(builder: RouteLocatorBuilder): RouteLocator = builder.routes().route("default_route") {
        it.path("/").filters { f ->
            f.filter { exchange, _ ->
                try {
                    circuitBreaker.executeSupplier {
                        val homeHtml = ClassPathResource("home.html")
                        val htmlContent = StreamUtils.copyToString(homeHtml.inputStream, StandardCharsets.UTF_8)
                        exchange.response.headers.contentType = MediaType.TEXT_HTML
                        exchange.response.writeWith(
                            Mono.just(
                                exchange.response.bufferFactory().wrap(htmlContent.toByteArray())
                            )
                        )
                    }
                } catch (e: HttpServerErrorException) {
                    logger.error("Circuit Breaker is open. Failed to read home.html file and write its content to the HTTP response.")
                    exchange.response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE)
                    return@filter exchange.response.setComplete()
                }
            }
        }.uri("http://localhost:8081")
    }.build()
}