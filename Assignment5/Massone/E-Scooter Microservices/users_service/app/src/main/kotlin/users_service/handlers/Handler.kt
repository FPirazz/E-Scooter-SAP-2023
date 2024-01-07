package users_service.handlers

import io.vertx.ext.web.RoutingContext

fun interface Handler {
    fun handle(routingContext: RoutingContext)
}