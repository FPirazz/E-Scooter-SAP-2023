package users_service.handlers

import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import users_service.db.DatabaseClient
import java.nio.file.Files
import java.nio.file.Paths

class RegisterHandler(
    private val dbClient: DatabaseClient,
) : Handler {
    override fun handle(routingContext: RoutingContext) {
        // print the request
        val request = routingContext.request()
        println("[VERTX_REGISTER_HANDLER] Received request: ${request.method()} ${request.uri()}")

        if (request.method().name() == "GET") {
            handleGet(routingContext)
        } else if (request.method().name() == "POST") {
            println("POST request")
            handlePost(routingContext)
        }
    }

    private fun handleGet(routingContext: RoutingContext) {
        // Handle GET request here
        val filePath =
            Paths.get("Assignment5/Massone/E-Scooter Microservices/users_service/app/src/main/resources/registration.html")
        val fileBytes = Files.readAllBytes(filePath)
        val fileContent = String(fileBytes)

        routingContext.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/html").end(fileContent)
    }

    private fun handlePost(routingContext: RoutingContext) {
        // Print the request
        val request = routingContext.request()
        println("[VERTX_REGISTER_HANDLER] Received request: ${request.method()} ${request.uri()}")

        // Get the user's information from the request
        val name = routingContext.request().getFormAttribute("name")
        val email = routingContext.request().getFormAttribute("email")
        val password = routingContext.request().getFormAttribute("password")

        // Check if any of the parameters are null
        if (name == null) {
            println("Name is null")
        }
        if (email == null) {
            println("Email is null")
        }
        if (password == null) {
            println("Password is null")
        }

        if (name == null || email == null || password == null) {
            routingContext.response().setStatusCode(400).end("Missing request parameters")
            return
        }

        // Create a User instance
        val user = models.User(name, email, password)

        // Create a JsonObject to store the user's information
        val userJson = JsonObject()
            .put("name", user.name)
            .put("email", user.email)
            .put("password", user.password)

        // Store the user's information in MongoDB
        dbClient.save("users", userJson) { ar ->
            if (ar.succeeded()) {
                println("User stored in MongoDB")
                // redirect to the users root
                routingContext.response().setStatusCode(302).putHeader("Location", "/users/").end()
            } else {
                println("Failed to store user in MongoDB: ${ar.cause()}")
            }
        }
    }
}