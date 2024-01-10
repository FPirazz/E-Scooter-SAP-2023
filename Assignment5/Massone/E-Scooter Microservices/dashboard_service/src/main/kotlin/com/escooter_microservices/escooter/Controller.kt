package com.escooter_microservices.escooter

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller {
    @GetMapping("/")
    fun home(): String {
        return "Welcome to the E-Scooter Microservices!"
    }

    @GetMapping("/dashboard/")
    fun dashboard(): String {
        return "Welcome to the Dashboard!"
    }
}