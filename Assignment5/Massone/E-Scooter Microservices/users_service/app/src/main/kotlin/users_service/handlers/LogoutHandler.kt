package users_service.handlers

import io.vertx.circuitbreaker.CircuitBreaker
import io.vertx.core.http.Cookie
import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.RoutingContext

class LogoutHandler(private val circuitBreaker: CircuitBreaker) : Handler {
    override fun handle(routingContext: RoutingContext) {
        circuitBreaker.execute<Any> { promise ->
            try {
                val cookie = Cookie.cookie("email", "")
                    .setMaxAge(0)
                    .setPath("/") // Set the path to match the original cookie's path
                    // Set the domain to match the original cookie's domain. Replace 'your.domain' with your actual domain.
                    .setDomain("localhost")

                routingContext.addCookie(cookie)

                println("[VERTX_USER_SERVICE] User logged out")

                // Redirect the user to the login page
                routingContext.response().putHeader(HttpHeaders.LOCATION, "/users/login").setStatusCode(302).end()
                promise.complete()
            } catch (e: Exception) {
                promise.fail(e)
            }
        }.onComplete { ar ->
            if (ar.failed()) {
                println("Failed to handle logout request: ${ar.cause()}")
            }
        }
    }
}