import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.ext.web.Router
import io.vertx.servicediscovery.ServiceDiscovery
import io.vertx.servicediscovery.types.HttpEndpoint
import users_service.handlers.HomeHandler
import users_service.handlers.RegisterHandler
import java.net.InetAddress

class UsersServiceVerticle : AbstractVerticle() {
    private lateinit var serviceDiscovery: ServiceDiscovery

    override fun start(startPromise: Promise<Void>) {
        serviceDiscovery = ServiceDiscovery.create(vertx)

        val router = Router.router(vertx)
        router.route("/*").handler { routingContext ->
            val request = routingContext.request()
            println("[VERTX_USER_SERVICE] Received request: ${request.method()} ${request.uri()}")
            routingContext.next()
        }
        router.get("/").handler(HomeHandler()::handle)
        router.get("/register").handler(RegisterHandler()::handle)


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

                    publishServiceRecord(ipAddress, port, startPromise)
                } else {
                    startPromise.fail(httpServerAsyncResult.cause())
                }
            }
    }

    private fun publishServiceRecord(ipAddress: String, port: Int, startPromise: Promise<Void>) {
        val record = HttpEndpoint.createRecord("users-service", ipAddress, port, "/")
        serviceDiscovery.publish(record) { publishAsyncResult ->
            if (publishAsyncResult.succeeded()) {
                println("Service published")
                startPromise.complete()
            } else {
                println("Service publication failed")
                startPromise.fail(publishAsyncResult.cause())
            }
        }
    }

    override fun stop(stopPromise: Promise<Void>) {
        serviceDiscovery.close()
        super.stop(stopPromise)
    }
}