package api_gateway.routes

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import java.util.logging.Logger

class MaintenanceServiceRoute(private val logger: Logger) {
fun route(builder: RouteLocatorBuilder): RouteLocator = builder.routes().route("maintainance_service_root_route") {
    it.path("/maintenance/").filters { f ->
            f.rewritePath("/maintenance/(?<segment>.*)", "/\$\\{segment}")
        }.uri("http://localhost:8082")
}.build()}