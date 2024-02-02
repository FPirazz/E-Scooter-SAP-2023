package com.escooter_microservices.escooter

import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView

@RestController
class Controller(private val rideService: RideService) {
    @GetMapping("/")
    fun home(): ModelAndView {
        return ModelAndView("home")
    }

    @GetMapping("/create_ride")
    fun createRide(): ModelAndView {
        return ModelAndView("create_ride")
    }

    @GetMapping("/get_rides")
    fun getRides(): ModelAndView {
        val modelAndView = ModelAndView("rides")
        modelAndView.addObject("rides", rideService.getRides())
        return modelAndView
    }

    @PostMapping("/remove_ride")
    fun removeRide(@RequestParam("id") id: String): ModelAndView {
        rideService.removeRide(id)
        return ModelAndView("ride_deleted")
    }

    @PostMapping("/create_ride")
    fun createRide(@ModelAttribute ride: Ride): ModelAndView {
        return try {
            rideService.createRide(ride)
            ModelAndView("ride_created")
        } catch (e: Exception) {
            ModelAndView("error_page") // replace with your actual error page
        }
    }
}