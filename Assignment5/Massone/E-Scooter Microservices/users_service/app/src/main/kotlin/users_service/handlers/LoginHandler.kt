package users_service.handlers

import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.web.RoutingContext
import java.nio.file.Files
import java.nio.file.Paths

class LoginHandler(
    private val mongoClient: MongoClient,
) : Handler {
    override fun handle(routingContext: RoutingContext) {
        val request = routingContext.request()
        println("[VERTX_LOGIN_HANDLER] Received request: ${request.method()} ${request.uri()}")

        if (request.method().name() == "GET") {
            handleGet(routingContext)
        } else if (request.method().name() == "POST") {
            handlePost(routingContext)
        }
    }

    private fun handleGet(routingContext: RoutingContext) {
        val filePath =
            Paths.get("Assignment5/Massone/E-Scooter Microservices/users_service/app/src/main/resources/login.html")
        val fileBytes = Files.readAllBytes(filePath)
        val fileContent = String(fileBytes)

        routingContext.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/html").end(fileContent)
    }

    private fun handlePost(routingContext: RoutingContext) {
        val email = routingContext.request().getFormAttribute("email")
        val password = routingContext.request().getFormAttribute("password")

        if (email == null || password == null) {
            routingContext.response().setStatusCode(400).end("Missing request parameters")
            return
        }

        val query = JsonObject().put("email", email)
        mongoClient.findOne("users", query, null) { ar ->
            if (ar.succeeded()) {
                val user = ar.result()
                if (user != null && user.getString("password") == password) {
                    // User is authenticated, handle the session here
                    routingContext.response().setStatusCode(200).end("User authenticated")
                } else {
                    routingContext.response().setStatusCode(401).end("Invalid email or password")
                }
            } else {
                routingContext.response().setStatusCode(500).end("Server error")
            }
        }
    }
}