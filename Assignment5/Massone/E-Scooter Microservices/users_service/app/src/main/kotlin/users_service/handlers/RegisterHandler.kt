package users_service.handlers

import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.RoutingContext
import java.nio.file.Files
import java.nio.file.Paths

class RegisterHandler : Handler {
    override fun handle(routingContext: RoutingContext) {
        // print the request
        val request = routingContext.request()
        println("[VERTX_REGISTER_HANDLER] Received request: ${request.method()} ${request.uri()}")

        val filePath =
            Paths.get("Assignment5/Massone/E-Scooter Microservices/users_service/app/src/main/resources/registration.html")
        val fileBytes = Files.readAllBytes(filePath)
        val fileContent = String(fileBytes)

        routingContext.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, "text/html")
            .end(fileContent)
    }
}