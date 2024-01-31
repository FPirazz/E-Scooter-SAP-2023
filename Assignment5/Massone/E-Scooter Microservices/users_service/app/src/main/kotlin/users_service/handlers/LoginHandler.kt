package users_service.handlers

import io.vertx.core.http.Cookie
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import users_service.db.DatabaseClient
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
        val filePath = Paths.get("Assignment5/Massone/E-Scooter Microservices/users_service/app/src/main/resources/login.html")
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
        dbClient.findOne("users", query, null) { ar ->
            if (ar.succeeded()) {
                val user = ar.result()
                if (user != null && user.getString("password") == password) {
                    // Create a cookie with the user's email
                    val userCookie = Cookie.cookie("email", user.getString("email"))
                    userCookie.setMaxAge(86400) // Set the cookie to expire after one day
                    userCookie.setPath("/")
                    // Set the cookie in the response
                    routingContext.response().addCookie(userCookie)
                    // Retrieve the cookie from the context and check if it is set correctly
                    val emailCookie = routingContext.cookieMap()["email"]
                    if (emailCookie != null) {
                        println("[LoginHandler] Cookie data: ${emailCookie.name} ${emailCookie.value} ${emailCookie.maxAge}")
                    }

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