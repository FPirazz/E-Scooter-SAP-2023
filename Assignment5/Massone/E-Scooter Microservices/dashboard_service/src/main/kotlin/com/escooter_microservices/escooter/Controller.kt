package com.escooter_microservices.escooter

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView

@Controller
class Controller {
    @GetMapping("/")
    fun home(): String {
        return "Welcome to the E-Scooter Microservices!"
    }

    @GetMapping("/dashboard/")
    fun dashboard(): ModelAndView {
        return ModelAndView("dashboard")
    }
}