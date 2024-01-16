package users_service.handlers

import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.RoutingContext

class LogoutHandler : Handler {
    override fun handle(routingContext: RoutingContext) {
        routingContext.removeCookie("email")

        println("[VERTX_USER_SERVICE] User logged out")

        // Redirect the user to the login page
        routingContext.response().putHeader(HttpHeaders.LOCATION, "/users/login").setStatusCode(302).end()
    }
}