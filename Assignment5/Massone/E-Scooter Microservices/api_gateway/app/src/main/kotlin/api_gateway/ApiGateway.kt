package api_gateway

import api_gateway.routes.DefaultRoute
import api_gateway.routes.MaintenanceServiceRoute
import api_gateway.routes.RidesServiceRoute
import api_gateway.routes.UsersServiceRoute
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean

@SpringBootApplication
class ApiGateway {
    @Autowired
    private lateinit var circuitBreaker: CircuitBreaker

    @Bean
    fun maintenersServiceRoute(builder: RouteLocatorBuilder): RouteLocator = MaintenanceServiceRoute(circuitBreaker).route(builder)

    @Bean
    fun usersServiceRoute(builder: RouteLocatorBuilder): RouteLocator = UsersServiceRoute(circuitBreaker).route(builder)

    @Bean
    fun ridesServiceRoute(builder: RouteLocatorBuilder): RouteLocator = RidesServiceRoute(circuitBreaker).route(builder)

    @Bean
    fun defaultRoute(builder: RouteLocatorBuilder): RouteLocator = DefaultRoute(circuitBreaker).route(builder)
}

fun main(args: Array<String>) {
    runApplication<ApiGateway>(*args)
}