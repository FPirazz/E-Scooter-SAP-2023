package api_gateway.routes

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.util.StreamUtils
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets
import java.util.logging.Logger

class MaintenanceServiceRoute(private val logger: Logger) {
    fun route(builder: RouteLocatorBuilder): RouteLocator = builder.routes()
        .route("maintainance_service_root_route") {
            it.path("/maintenance/**").filters { f ->
                f.filter { exchange, chain ->
                    val request = exchange.request
                    // Check if email cookie is set
                    val isMaintainerCookie = request.cookies["isMaintainer"]
                    isMaintainerCookie?.let {
                        logger.info(" [Default Route] Email cookie is set. Request: ${request.uri}")
                    } ?: run {
                        logger.info(" [Default Route] Email cookie is not set. Redirecting to login page.")
                        val redirectHtml = ClassPathResource("redirect.html")
                        val htmlContent = StreamUtils.copyToString(redirectHtml.inputStream, StandardCharsets.UTF_8)
                        exchange.response.headers.contentType = MediaType.TEXT_HTML
                        return@filter exchange.response.writeWith(
                                Mono.just(
                                        exchange.response.bufferFactory().wrap(htmlContent.toByteArray())
                                )
                        )
                    }
                    chain.filter(exchange)
                }
                f.rewritePath("/maintenance/(?<segment>.*)", "/\$\\{segment}")
            }.uri("http://localhost:8082")
        }.build()
}