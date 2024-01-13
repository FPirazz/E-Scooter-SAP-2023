package users_service.handlers

import io.vertx.core.http.HttpHeaders
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.web.RoutingContext
import java.nio.file.Files
import java.nio.file.Paths

class HomeHandler(
    private val mongoClient: MongoClient,
) : Handler {
    override fun handle(routingContext: RoutingContext) {
        val filePath =
            Paths.get("Assignment5/Massone/E-Scooter Microservices/users_service/app/src/main/resources/home.html")
        val fileBytes = Files.readAllBytes(filePath)
        val fileContent = String(fileBytes)

        routingContext.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, "text/html")
            .end(fileContent)
    }
}