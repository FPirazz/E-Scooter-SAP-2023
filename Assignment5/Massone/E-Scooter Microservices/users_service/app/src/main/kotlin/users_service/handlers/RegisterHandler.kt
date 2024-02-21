package users_service.handlers

import io.vertx.circuitbreaker.CircuitBreaker
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import users_service.db.DatabaseClient
import users_service.models.User
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * This class is responsible for handling registration requests.
 * It uses a Circuit Breaker to prevent the system from being overwhelmed by requests.
 * It handles both GET and POST requests.
 *
 * @property dbClient The database client used to interact with the database.
 * @property circuitBreaker The Circuit Breaker used to prevent the system from being overwhelmed by requests.
 */
class RegisterHandler(
    private val dbClient: DatabaseClient, private val circuitBreaker: CircuitBreaker,
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
        circuitBreaker.execute<Any> { promise ->
            try {
                val inputStream = javaClass.getResourceAsStream("/registration.html")
                val reader = inputStream?.let { InputStreamReader(it) }?.let { BufferedReader(it) }
                val fileContent = reader?.readText()

                routingContext.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/html").end(fileContent)
                promise.complete()
            } catch (e: Exception) {
                promise.fail(e)
            }
        }.onComplete { ar ->
            if (ar.failed()) {
                println("Failed to handle GET request: ${ar.cause()}")
            }
        }
    }

    private fun handlePost(routingContext: RoutingContext) {
        val (name, email, password, isMaintainer) = getFormAttributes(routingContext) ?: return

        val user = User(name, email, password, isMaintainer)

        val userJson = JsonObject().put("name", user.name).put("email", user.email).put("password", user.password)
            .put("maintainer", user.isMaintainer)

        circuitBreaker.execute<Any> { promise ->
            dbClient.save("users", userJson) { ar ->
                if (ar.succeeded()) {
                    promise.complete()
                } else {
                    promise.fail(ar.cause())
                }
            }
        }.onComplete { ar ->
            if (ar.succeeded()) {
                routingContext.response().setStatusCode(302).putHeader("Location", "/users/").end()
            } else {
                routingContext.fail(ar.cause())
            }
        }
    }

    private fun getFormAttributes(routingContext: RoutingContext): UserForm? {
        val name = routingContext.request().getFormAttribute("name")
        val email = routingContext.request().getFormAttribute("email")
        val password = routingContext.request().getFormAttribute("password")
        val isMaintainer = routingContext.request().getFormAttribute("maintainer") == "true"

        val isAnyFieldEmpty = name == null || email == null || password == null
        if (isAnyFieldEmpty) {
            routingContext.response().setStatusCode(400).end("Missing request parameters")
            return null
        }

        return UserForm(name, email, password, isMaintainer)
    }
}

data class UserForm(val name: String, val email: String, val password: String, val isMaintainer: Boolean)