package users_service

import io.vertx.core.Vertx

class UserServiceApp {
    fun run() {
        val vertx = Vertx.vertx()
        vertx.deployVerticle(UsersServiceVerticle())
    }
}

fun main() {
    val app = UserServiceApp()
    app.run()
}