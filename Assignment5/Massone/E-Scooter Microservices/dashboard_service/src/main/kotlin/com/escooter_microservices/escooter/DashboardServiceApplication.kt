package com.escooter_microservices.escooter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DashboardServiceApplication

fun main(args: Array<String>) {
    runApplication<DashboardServiceApplication>(*args)
}
