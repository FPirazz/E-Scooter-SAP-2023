package users_service

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.redis.client.Redis
import io.vertx.redis.client.RedisAPI
import io.vertx.redis.client.RedisOptions
import users_service.handlers.HomeHandler
import users_service.handlers.RegisterHandler
import java.net.InetAddress

class UsersServiceVerticle : AbstractVerticle() {
    private lateinit var redis: RedisAPI

    override fun start(startPromise: Promise<Void>) {
        val redisClient = Redis.createClient(vertx, RedisOptions())
        redis = RedisAPI.api(redisClient)

        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.route("/*").handler { routingContext ->
            val request = routingContext.request()
            println("[VERTX_USER_SERVICE] Received request: ${request.method()} ${request.uri()}")
            routingContext.next()
        }
        router.get("/").handler(HomeHandler()::handle)
        router.post("/register").handler(RegisterHandler(redis)::handle)
        router.get("/register").handler(RegisterHandler(redis)::handle)

        createHttpServer(router, startPromise)
    }

    private fun createHttpServer(router: Router, startPromise: Promise<Void>) {
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(8888) { httpServerAsyncResult ->
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