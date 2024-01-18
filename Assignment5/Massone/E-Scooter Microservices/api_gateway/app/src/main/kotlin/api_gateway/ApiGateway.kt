package api_gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import java.util.logging.Logger

@SpringBootApplication
class ApiGateway {
    private val logger = Logger.getLogger(ApiGateway::class.java.name)

    @Bean
    fun usersServiceRoute(builder: RouteLocatorBuilder): RouteLocator =
        builder.routes().route("users_service_root_route") {
            it.path("/users/**").filters { f ->
                f.filter { exchange, chain ->
                    logger.info(" [Users Service] Request: ${exchange.request.uri}")
                    chain.filter(exchange)
                }
                f.rewritePath("/users/(?<segment>.*)", "/\$\\{segment}")
            }.uri("http://localhost:8888")
        }.build()

    @Bean
    fun ridesServiceRoute(builder: RouteLocatorBuilder): RouteLocator =
        builder.routes().route("rides_service_root_route") {
            it.path("/rides/**").filters { f ->
                f.filter { exchange, chain ->
                    logger.info(" [Rides Service] Request: ${exchange.request.uri}")
                    chain.filter(exchange)
                }
                f.rewritePath("/rides/(?<segment>.*)", "/\$\\{segment}")
            }.uri("http://localhost:8081")
        }.build()
    @Bean
    fun defaultRoute(builder: RouteLocatorBuilder): RouteLocator = builder.routes().route("default_route") {
        it.path("/**").uri("http://localhost:8081")
    }.build()
}

fun main(args: Array<String>) {
    runApplication<ApiGateway>(*args)
}