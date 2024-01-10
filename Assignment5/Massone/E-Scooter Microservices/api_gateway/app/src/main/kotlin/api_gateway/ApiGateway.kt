package api_gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean

@SpringBootApplication
class ApiGateway {
    @Bean
    fun routes(builder: RouteLocatorBuilder): RouteLocator = builder.routes()
        .route("users_service_root_route") {
            it.path("/users/**").filters { f -> f.rewritePath("/users/(?<segment>.*)", "/\$\\{segment}") }
                .uri("http://localhost:8888")
        }
        .route("dashboard_service_root_route") { it.path("/dashboard/**").uri("http://localhost:8081") }
        .build()
}

fun main(args: Array<String>) {
    runApplication<ApiGateway>(*args)
}