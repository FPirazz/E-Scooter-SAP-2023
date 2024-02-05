package api_gateway.routes

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.util.StreamUtils
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets

class MaintenanceServiceRoute(private val circuitBreaker: CircuitBreaker) {
    private val logger = LoggerFactory.getLogger(MaintenanceServiceRoute::class.java)

    companion object {
        private const val MAINTENANCE_ROUTE = "/maintenance/**"
        private const val REDIRECT_HTML = "redirect.html"
        private const val IS_MAINTAINER = "isMaintainer"
    }

    fun route(builder: RouteLocatorBuilder): RouteLocator = builder.routes().route("maintainance_service_root_route") {
        it.path(MAINTENANCE_ROUTE).filters { f ->
            f.filter(this::applyFilter)
            f.rewritePath("/maintenance/(?<segment>.*)", "/\$\\{segment}")
        }.uri("http://localhost:8082")
    }.build()

    private fun applyFilter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val request = exchange.request

        // Log all incoming requests
        logger.info("Incoming request: ${request.method} ${request.uri}")

        // Check if the request is a PUT request
        if (request.method == HttpMethod.PUT) {
            logger.info("Incoming PUT request: ${request.uri}")
        }

        // Check if email cookie is set for non-PUT requests
        if (request.method != HttpMethod.PUT) {
            val isMaintainerCookie = request.cookies[IS_MAINTAINER]
            isMaintainerCookie?.let { logger.info("Request with correct cookies: ${request.uri}") } ?: run {
                logger.info("Request without correct cookies: ${request.uri}")
                return@applyFilter redirectToLogin(exchange)
            }
        }

        return try {
            circuitBreaker.executeSupplier {
                chain.filter(exchange)
            }
        } catch (e: HttpServerErrorException) {
            logger.error("Circuit Breaker is open. Failed to handle incoming request.")
            exchange.response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE)
            return exchange.response.setComplete()
        }
    }

    private fun redirectToLogin(exchange: ServerWebExchange): Mono<Void> {
        val redirectHtml = ClassPathResource(REDIRECT_HTML)
        val htmlContent = StreamUtils.copyToString(redirectHtml.inputStream, StandardCharsets.UTF_8)
        exchange.response.headers.contentType = MediaType.TEXT_HTML
        return exchange.response.writeWith(
            Mono.just(exchange.response.bufferFactory().wrap(htmlContent.toByteArray()))
        )
    }
}