package users_service

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import users_service.handlers.HomeHandler
import users_service.handlers.RegisterHandler
import java.net.InetAddress

class UsersServiceVerticle : AbstractVerticle() {
    private lateinit var mongoClient: MongoClient

    override fun start(startPromise: Promise<Void>) {
        val mongoConfig = JsonObject().put("connection_string", "mongodb://localhost:27017").put("db_name", "Escooters")
        mongoClient = MongoClient.createShared(vertx, mongoConfig)

        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create()) // Add this line to add a BodyHandler
        router.route("/*").handler { routingContext ->
            val request = routingContext.request()
            println("[VERTX_USER_SERVICE] Received request: ${request.method()} ${request.uri()}")
            routingContext.next()
        }
        router.get("/").handler(HomeHandler(mongoClient)::handle)
        router.get("/register").handler(RegisterHandler(mongoClient)::handle)
        router.post("/register").handler(RegisterHandler(mongoClient)::handle)

        createHttpServer(router, startPromise)
    }

    private fun createHttpServer(router: Router, startPromise: Promise<Void>) {
        vertx.createHttpServer().requestHandler(router).listen(8888) { httpServerAsyncResult ->
                if (httpServerAsyncResult.succeeded()) {
                    val ipAddress = InetAddress.getLocalHost().hostAddress
                    val port = httpServerAsyncResult.result().actualPort()
                    println("Server is listening on IP address: $ipAddress:$port")
                } else {
                    startPromise.fail(httpServerAsyncResult.cause())
                }
            }
    }

    override fun stop(stopPromise: Promise<Void>) {
        super.stop(stopPromise)
    }
}