package users_service

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.SessionHandler
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.ext.web.sstore.LocalSessionStore
import users_service.db.DatabaseClient
import users_service.db.MongoDatabaseClient
import users_service.handlers.HomeHandler
import users_service.handlers.LoginHandler
import users_service.handlers.LogoutHandler
import users_service.handlers.RegisterHandler
import java.net.InetAddress

class UsersServiceVerticle : AbstractVerticle() {
    private lateinit var databaseClient: DatabaseClient

    override fun start(startPromise: Promise<Void>) {
        val mongoConfig = JsonObject().put("connection_string", "mongodb://localhost:27017").put("db_name", "Users_Service")
        val mongoClient = MongoClient.createShared(vertx, mongoConfig)
        databaseClient = MongoDatabaseClient(mongoClient)

        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)))
        router.get("/").handler(HomeHandler(databaseClient)::handle)
        router.get("/register").handler(RegisterHandler(databaseClient)::handle)
        router.post("/register").handler(RegisterHandler(databaseClient)::handle)
        router.get("/login").handler(LoginHandler(databaseClient)::handle)
        router.post("/login").handler(LoginHandler(databaseClient)::handle)
        router.post("/logout").handler(LogoutHandler()::handle)

        // Add a custom handler to log request information
        router.route("/scripts/*").handler { routingContext ->
            val request = routingContext.request()
            println("Received request for static file: ${request.method()} ${request.uri()}")
            routingContext.next()
        }.handler(StaticHandler.create("scripts"))

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