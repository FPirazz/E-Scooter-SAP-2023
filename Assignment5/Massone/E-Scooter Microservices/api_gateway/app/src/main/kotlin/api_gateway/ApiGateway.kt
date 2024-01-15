package api_gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean

@SpringBootApplication
class ApiGateway {
    @Bean
    fun usersServiceRoute(builder: RouteLocatorBuilder): RouteLocator =
        builder.routes()
            .route("users_service_root_route") {
                it.path("/users/**")
                    .filters { f ->
                        f.rewritePath("/users/(?<segment>.*)", "/\$\\{segment}")
                    }
                    .uri("http://localhost:8888")
            }
            .build()

    @Bean
    fun ridesServiceRoute(builder: RouteLocatorBuilder): RouteLocator =
        builder.routes()
            .route("rides_service_root_route") {
                it.path("/rides/**").uri("http://localhost:8081")
            }
            .build()

    @Bean
    fun defaultRoute(builder: RouteLocatorBuilder): RouteLocator =
        builder.routes()
            .route("default_route") {
                it.path("/**")
                    .uri("http://localhost:8081")
            }
            .build()
}

fun main(args: Array<String>) {
    runApplication<ApiGateway>(*args)
}