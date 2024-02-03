package com.escooter_microservices.escooter.controllers

import com.escooter_microservices.escooter.models.Ride
import com.escooter_microservices.escooter.services.RideService
import org.slf4j.LoggerFactory
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.util.UriComponentsBuilder

@RestController
class RidesController(private val rideService: RideService) {
    private val logger = LoggerFactory.getLogger(RidesController::class.java)

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
    fun createRide(@ModelAttribute ride: Ride, @RequestParam("scooterId") scooterId: String): ModelAndView {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set(HttpHeaders.ORIGIN, "http://localhost:8080")

        val map = mutableMapOf<String, String>()
        map["scooterId"] = scooterId

        val uriComponentsBuilder =
            UriComponentsBuilder.fromHttpUrl("http://localhost:8080/maintenance/use_scooter/$scooterId/")
        map.forEach { (k, v) ->
            uriComponentsBuilder.queryParam(k, v)
        }

        val uri = uriComponentsBuilder.build(false).toUri()

        val request = HttpEntity<String>(headers)
        val responseEntity: ResponseEntity<String> =
            RestTemplate().exchange(uri, HttpMethod.PUT, request, String::class.java)

        return if (responseEntity.statusCode == HttpStatus.OK) {
            try {
                logger.info("Put request to maintenance service successful")
                rideService.createRide(ride)
                ModelAndView("ride_created")
            } catch (e: Exception) {
                ModelAndView("error_page") // replace with your actual error page
            }
        } else {
            ModelAndView("error_page") // replace with your actual error page
        }
    }
}