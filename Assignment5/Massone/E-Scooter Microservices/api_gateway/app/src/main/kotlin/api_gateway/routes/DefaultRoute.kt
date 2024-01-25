package api_gateway.routes

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.util.StreamUtils
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets
import java.util.logging.Logger

class DefaultRoute(private val logger: Logger) {
    fun route(builder: RouteLocatorBuilder): RouteLocator = builder
        .routes()
        .route("default_route") {
            it.path("/")
                .filters { f ->
                    f.filter { exchange, _ ->
                        val homeHtml = ClassPathResource("home.html")
                        val htmlContent = StreamUtils.copyToString(homeHtml.inputStream, StandardCharsets.UTF_8)
                        exchange.response.headers.contentType = MediaType.TEXT_HTML
                        return@filter exchange.response.writeWith(
                            Mono.just(
                                exchange.response.bufferFactory().wrap(htmlContent.toByteArray())
                            )
                        )
                    }
                }.uri("http://localhost:8081")
        }.build()
}