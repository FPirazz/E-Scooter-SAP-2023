package users_service.handlers

import io.vertx.circuitbreaker.CircuitBreaker
import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.RoutingContext
import users_service.db.DatabaseClient
import java.io.BufferedReader
import java.io.InputStreamReader

class HomeHandler(
    private val dbClient: DatabaseClient,
) : Handler {
    override fun handle(routingContext: RoutingContext) {
        val inputStream = javaClass.getResourceAsStream("/home.html")
        val reader = BufferedReader(InputStreamReader(inputStream))
        var fileContent = reader.readText()

        // Retrieve the user's email from the cookie
        routingContext.cookieMap()["email"]?.let { emailCookie ->
            // If the cookie exists and is not expired, add the email to the welcome message
            val email = emailCookie.value
            fileContent = fileContent.replace("Welcome to the User Service!", "Welcome to the User Service, $email!")
        }
        routingContext.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/html").end(fileContent)
    }
}