package users_service.handlers

import io.vertx.ext.web.RoutingContext

class HomeHandler : Handler {
    override fun handle(routingContext: RoutingContext) {
        routingContext.response()
            .putHeader("content-type", "text/plain")
            .end("Welcome to the Users Service!")
    }
}