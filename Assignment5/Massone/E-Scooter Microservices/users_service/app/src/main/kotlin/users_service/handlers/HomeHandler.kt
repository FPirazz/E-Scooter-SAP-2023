package users_service.handlers

import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.RoutingContext
import io.vertx.core.json.JsonObject
import users_service.db.DatabaseClient
import java.nio.file.Files
import java.nio.file.Paths

class HomeHandler(
    private val dbClient: DatabaseClient,
) : Handler {
    override fun handle(routingContext: RoutingContext) {
        val filePath =
            Paths.get("Assignment5/Massone/E-Scooter Microservices/users_service/app/src/main/resources/home.html")
        val fileBytes = Files.readAllBytes(filePath)
        var fileContent = String(fileBytes)

        // Retrieve the user's information from the session
        val user = routingContext.session().get("user") as JsonObject?
        if (user != null) {
            // If the user is logged in, add their email to the welcome message
            val email = user.getString("email")
            fileContent = fileContent.replace("Welcome to the User Service!", "Welcome to the User Service, $email!")
        } else {
            // If the user is not logged in, redirect to the login page
            println("User is not logged in")
            println("Session data: ${routingContext.session().data()}")
        }

        routingContext.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, "text/html")
            .end(fileContent)
    }
}