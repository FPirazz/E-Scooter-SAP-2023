package com.escooter_microservices.escooter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@SpringBootApplication
@EnableWebMvc
class RidesServiceApp

fun main(args: Array<String>) {
    runApplication<RidesServiceApp>(*args)
}