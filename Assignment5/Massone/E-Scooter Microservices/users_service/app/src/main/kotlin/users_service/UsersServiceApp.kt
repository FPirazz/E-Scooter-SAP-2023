package users_service

import UsersServiceVerticle
import io.vertx.core.Vertx

fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(UsersServiceVerticle())
}