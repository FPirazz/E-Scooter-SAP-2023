package api_gateway.routes

import java.nio.charset.StandardCharsets
import java.util.logging.Logger
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.util.StreamUtils
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class MaintenanceServiceRoute(private val logger: Logger) {
    companion object {
        private const val MAINTENANCE_ROUTE = "/maintenance/**"
        private const val REDIRECT_HTML = "redirect.html"
    }

    fun route(builder: RouteLocatorBuilder): RouteLocator =
            builder.routes()
                    .route("maintainance_service_root_route") {
                        it.path(MAINTENANCE_ROUTE)
                                .filters { f ->
                                    f.filter(this::applyFilter)
                                    f.rewritePath("/maintenance/(?<segment>.*)", "/\$\\{segment}")
                                }
                                .uri("http://localhost:8082")
                    }
                    .build()

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
            val isMaintainerCookie = request.cookies["isMaintainer"]
            isMaintainerCookie?.let { logger.info("Request with correct cookies: ${request.uri}") }
                    ?: run {
                        logger.info("Request without correct cookies: ${request.uri}")
                        val redirectHtml = ClassPathResource(REDIRECT_HTML)
                        val htmlContent =
                                StreamUtils.copyToString(
                                        redirectHtml.inputStream,
                                        StandardCharsets.UTF_8
                                )
                        exchange.response.headers.contentType = MediaType.TEXT_HTML
                        return@applyFilter exchange.response.writeWith(
                                Mono.just(
                                        exchange.response
                                                .bufferFactory()
                                                .wrap(htmlContent.toByteArray())
                                )
                        )
                    }
        }

        return chain.filter(exchange)
    }
}
