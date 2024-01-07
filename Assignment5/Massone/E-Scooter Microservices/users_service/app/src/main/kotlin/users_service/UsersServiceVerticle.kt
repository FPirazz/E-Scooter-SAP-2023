package users_service

import io.vertx.core.AbstractVerticle
import io.vertx.core.AsyncResult
import io.vertx.core.Promise
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Router
import users_service.handlers.HomeHandler
import java.net.InetAddress

class UsersServiceVerticle : AbstractVerticle() {
    override fun start(startPromise: Promise<Void>) {
        val router = Router.router(vertx)

        router.get("/").handler(HomeHandler()::handle)

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(8888) { http ->
                if (http.succeeded()) {
                    startPromise.complete()
                    printIPAndPort(http)
                } else {
                    startPromise.fail(http.cause());
                }
            }
    }

    private fun printIPAndPort(http: AsyncResult<HttpServer>) {
        val ip = InetAddress.getLocalHost().hostAddress
        val port = http.result().actualPort()
        println("Server is listening on IP address: $ip:$port")
    }
}

