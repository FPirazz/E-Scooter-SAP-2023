package api_gateway

import api_gateway.routes.DefaultRoute
import api_gateway.routes.RidesServiceRoute
import api_gateway.routes.UsersServiceRoute
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import java.util.logging.Logger

@SpringBootApplication
class ApiGateway {
    private val logger = Logger.getLogger(ApiGateway::class.java.name)

    @Bean
    fun usersServiceRoute(builder: RouteLocatorBuilder): RouteLocator = UsersServiceRoute(logger).route(builder)

    @Bean
    fun ridesServiceRoute(builder: RouteLocatorBuilder): RouteLocator = RidesServiceRoute(logger).route(builder)

    @Bean
    fun defaultRoute(builder: RouteLocatorBuilder): RouteLocator = DefaultRoute().route(builder)
}

fun main(args: Array<String>) {
    runApplication<ApiGateway>(*args)
}