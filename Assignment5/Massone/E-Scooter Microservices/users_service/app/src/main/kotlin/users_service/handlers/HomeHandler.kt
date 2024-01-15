package users_service.handlers

import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.RoutingContext
import users_service.db.DatabaseClient
import java.nio.file.Files
import java.nio.file.Paths

class HomeHandler(
    private val dbClient: DatabaseClient,
) : Handler {
    override fun handle(routingContext: RoutingContext) {
        val filePath = Paths.get("Massone/E-Scooter Microservices/users_service/app/src/main/resources/home.html")
        val fileBytes = Files.readAllBytes(filePath)
        var fileContent = String(fileBytes)

        // Retrieve the user's email from the cookie
        val emailCookie = routingContext.cookieMap()["email"]
        if (emailCookie != null) {
            // If the cookie exists, add the email to the welcome message
            val email = emailCookie.value
            fileContent = fileContent.replace("Welcome to the User Service!", "Welcome to the User Service, $email!")
        } else {
            // If the cookie does not exist, redirect to the login page
            println("User is not logged in")
            println("Session data: ${routingContext.session().data()}")
        }

        routingContext.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/html").end(fileContent)
    }
}