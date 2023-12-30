package com.escooter_microservices.escooter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EscooterApplication

fun main(args: Array<String>) {
	runApplication<EscooterApplication>(*args)
}
