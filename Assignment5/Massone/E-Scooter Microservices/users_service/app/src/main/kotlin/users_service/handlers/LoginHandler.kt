package users_service.handlers

import io.vertx.core.http.Cookie
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import users_service.db.DatabaseClient
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths

class LoginHandler(
    private val dbClient: DatabaseClient,
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

        val inputStream = javaClass.getResourceAsStream("/login.html")
        val reader = inputStream?.let { InputStreamReader(it) }?.let { BufferedReader(it) }
        val fileContent = reader?.readText()

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
        dbClient.findOne("users", query, null) { ar ->
            if (ar.succeeded()) {
                val user = ar.result()
                if (user != null && user.getString("password") == password) {
                    // Create a cookie with the user's email
                    val emailCookie = Cookie.cookie("email", user.getString("email"))
                    emailCookie.setMaxAge(86400) // Set the cookie to expire after one day
                    emailCookie.setPath("/")

                    // Create a separate cookie to indicate if the user is a maintainer
                    val isMaintainer = user.getBoolean("maintainer") ?: false
                    val maintainerCookie = Cookie.cookie("isMaintainer", isMaintainer.toString())
                    maintainerCookie.setMaxAge(86400) // Set the cookie to expire after one day
                    maintainerCookie.setPath("/")

                    // Set the cookies in the response
                    routingContext.response().addCookie(emailCookie)
                    routingContext.response().addCookie(maintainerCookie)

                    // Log the information
                    println("[LoginHandler] Cookies set: Email: ${user.getString("email")}, Is Maintainer: $isMaintainer")

                    // Redirect to the home page
                    routingContext.response().setStatusCode(302).putHeader("Location", "/users/").end()
                } else {
                    routingContext.response().setStatusCode(401).end("Invalid email or password")
                }
            } else {
                routingContext.response().setStatusCode(500).end("Server error")
            }
        }

    }

}