package users_service.handlers

import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.RoutingContext
import io.vertx.redis.client.RedisAPI
import java.nio.file.Files
import java.nio.file.Paths

class RegisterHandler(private val redis: RedisAPI) : Handler {
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
        // Prin the request
        val request = routingContext.request()
        println("[VERTX_REGISTER_HANDLER] Received request: ${request.method()} ${request.uri()}")

        // Handle POST request here

        // get the user's information from the request
        val name = routingContext.request().getParam("name")
        val email = routingContext.request().getParam("email")
        val password = routingContext.request().getParam("password")

        // create a User instance
        val user = models.User(name, email, password)

        // store the user's information in Redis
        val hmsetArgs = listOf(
            email,
            "name", user.name,
            "email", user.email,
            "password", user.password
        )
        redis.hmset(hmsetArgs) { ar ->
            if (ar.succeeded()) {
                println("User stored in Redis")
            } else {
                println("Failed to store user in Redis: ${ar.cause()}")
            }
        }
    }
}