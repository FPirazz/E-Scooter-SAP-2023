package users_service.handlers

import io.vertx.core.http.HttpHeaders
import io.vertx.core.http.Cookie
import io.vertx.ext.web.RoutingContext

class LogoutHandler : Handler {
    override fun handle(routingContext: RoutingContext) {
        val cookie = Cookie.cookie("email", "")
            .setMaxAge(0)
            .setPath("/") // Set the path to match the original cookie's path
            // Set the domain to match the original cookie's domain. Replace 'your.domain' with your actual domain.
            .setDomain("localhost")

        routingContext.addCookie(cookie)

        println("[VERTX_USER_SERVICE] User logged out")

        // Redirect the user to the login page
        routingContext.response().putHeader(HttpHeaders.LOCATION, "/users/login").setStatusCode(302).end()
    }
}