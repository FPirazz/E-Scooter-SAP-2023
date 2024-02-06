package users_service.handlers

import io.vertx.circuitbreaker.CircuitBreaker
import io.vertx.core.http.Cookie
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import users_service.db.DatabaseClient
import java.io.BufferedReader
import java.io.InputStreamReader

class LoginHandler(
    private val dbClient: DatabaseClient,
    private val circuitBreaker: CircuitBreaker,
) : Handler {
    override fun handle(routingContext: RoutingContext) {
        val request = routingContext.request()

        if (request.method().name() == "GET") {
            handleGet(routingContext)
        } else if (request.method().name() == "POST") {
            handlePost(routingContext)
        }
    }

    private fun handleGet(routingContext: RoutingContext) {
        circuitBreaker.execute<Any> { promise ->
            try {
                val inputStream = javaClass.getResourceAsStream("/login.html")
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
        val email = routingContext.request().getFormAttribute("email")
        val password = routingContext.request().getFormAttribute("password")

        if (email == null || password == null) {
            routingContext.response().setStatusCode(400).end("Missing request parameters")
            return
        }

        circuitBreaker.execute<Any> { promise ->
            dbClient.findOne("users", JsonObject().put("email", email), null) { ar ->
                if (ar.succeeded()) {
                    promise.complete(ar.result())
                } else {
                    promise.fail(ar.cause())
                }
            }
        }.onComplete { ar ->
            if (ar.succeeded()) {
                val user = ar.result() as JsonObject?
                if (user != null && user.getString("password") == password) {
                    setCookies(routingContext, user)
                    routingContext.response().setStatusCode(302).putHeader("Location", "/users/").end()
                } else {
                    routingContext.response().setStatusCode(401).end("Invalid email or password")
                }
            } else {
                routingContext.response().setStatusCode(500).end("Server error")
            }
        }
    }

    private fun setCookies(routingContext: RoutingContext, user: JsonObject) {
        val emailCookie = Cookie.cookie("email", user.getString("email")).setMaxAge(86400).setPath("/")
        val maintainerCookie =
            Cookie.cookie("isMaintainer", user.getBoolean("maintainer").toString()).setMaxAge(86400).setPath("/")

        routingContext.response().addCookie(emailCookie).addCookie(maintainerCookie)
        println("[LoginHandler] Cookies set: Email: ${user.getString("email")}, Is Maintainer: ${user.getBoolean("maintainer")}")
    }
}