package com.escooter_microservices.escooter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@SpringBootApplication
@EnableWebMvc
class DashboardServiceApp

fun main(args: Array<String>) {
    runApplication<DashboardServiceApp>(*args)
}