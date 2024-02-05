package com.escooter_microservices.escooter.controllers


import com.escooter_microservices.escooter.models.Ride
import com.escooter_microservices.escooter.services.RideService
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.util.UriComponentsBuilder

@Controller
class RidesController(
    private val rideService: RideService,
    private val circuitBreaker: CircuitBreaker,
) {
    private val logger = LoggerFactory.getLogger(RidesController::class.java)

    /**
     * Handles the HTTP GET request for the root route ("/") and returns a ModelAndView object for the home page.
     */
    @GetMapping("/")
    fun home(): ModelAndView {
        return ModelAndView("home")
    }

    /**
     * Handles the HTTP GET request for the "/create_ride" route and returns a ModelAndView object for the create ride page.
     */
    @GetMapping("/create_ride")
    fun createRide(): ModelAndView {
        return ModelAndView("create_ride")
    }

    /**
     * Handles the HTTP GET request for the "/get_rides" route, retrieves the rides from the RideService,
     * and returns a ModelAndView object for the rides page.
     */
    @GetMapping("/get_rides")
    fun getRides(model: Model): ModelAndView {
        model.addAttribute("rides", rideService.getRides())
        return ModelAndView("rides")
    }

    /**
     * Handles the HTTP POST request for the "/remove_ride" route, removes the ride with the specified ID using the RideService,
     * and returns a ModelAndView object for the ride deleted page.
     */
    @PostMapping("/remove_ride")
    fun removeRide(@RequestParam("id") id: String): ModelAndView {
        rideService.removeRide(id)
        return ModelAndView("ride_deleted")
    }

    /**
     * Handles the HTTP POST request for the "/create_ride" route. It sends a PUT request to a maintenance service to use a scooter with the specified ID,
     * creates a ride using the RideService, and returns a ModelAndView object for the ride created page.
     */
    @PostMapping("/create_ride")
    fun createRide(@ModelAttribute ride: Ride, @RequestParam("scooterId") scooterId: String): ModelAndView {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set(HttpHeaders.ORIGIN, "http://localhost:8080")
        }

        val params = mutableMapOf("scooterId" to scooterId)
        val multiValueMap = LinkedMultiValueMap<String, String>()
        params.forEach { (key, value) -> multiValueMap.add(key, value) }

        val uri = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/maintenance/use_scooter/$scooterId/")
            .queryParams(multiValueMap).build(false).toUri()
        val request = HttpEntity<String>(headers)

        return try {
            circuitBreaker.executeSupplier {
                RestTemplate().exchange(uri, HttpMethod.PUT, request, String::class.java)
                logger.info("Put request to maintenance service successful")
                rideService.createRide(ride)
                ModelAndView("ride_created")
            }
        } catch (e: HttpServerErrorException) {
            ModelAndView("circuit_breaker_open") // redirect to circuit breaker open page
        }
    }
}