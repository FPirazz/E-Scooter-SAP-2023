package com.escooter_microservices.escooter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DashboardServiceApp

fun main(args: Array<String>) {
    runApplication<DashboardServiceApp>(*args)
}
