package maintenance_service.controllers

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import maintenance_service.models.Scooter
import maintenance_service.repositories.ScooterRepository
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.servlet.ModelAndView

@RestController
class ScooterController(
    private val scooterRepository: ScooterRepository,
    private val circuitBreaker: CircuitBreaker,
) {
    private val logger = LoggerFactory.getLogger(ScooterController::class.java)

    data class ScooterStateResponse(val state: String)

    @PostMapping("/create_escooter/")
    fun createScooter(@RequestBody scooter: Scooter): ModelAndView {
        return try {
            circuitBreaker.executeSupplier {
                if (scooter.name == null || scooter.location == null) {
                    ModelAndView("escooter_error")
                } else {
                    scooterRepository.save(scooter)
                    ModelAndView("escooter_created")
                }
            }
        } catch (e: Exception) {
            ModelAndView("error_page")
        }
    }

    @GetMapping("/escooter_created/")
    fun scooterCreated(): ModelAndView {
        return circuitBreaker.executeSupplier {
            ModelAndView("escooter_created")
        }
    }

    @GetMapping("/all_scooters/")
    fun getAllScooters(): List<Scooter> {
        return circuitBreaker.executeSupplier {
            scooterRepository.findAll()
        }
    }


    @GetMapping("/get_scooter_state/{scooterId}/")
    fun getScooterState(@PathVariable scooterId: String): ResponseEntity<ScooterStateResponse> {
        return try {
            circuitBreaker.executeSupplier {
                val scooterOptional = scooterRepository.findById(scooterId)
                if (scooterOptional.isPresent) {
                    val scooter = scooterOptional.get()
                    ResponseEntity.ok(ScooterStateResponse(scooter.state ?: "N/A"))
                } else {
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body(ScooterStateResponse("error"))
                }
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ScooterStateResponse("error"))
        }
    }

    /**
     * Handles GET requests to the "/available_scooters/" endpoint. Makes an HTTP GET request to the
     * "http://localhost:8080/scooters/available_scooters" URL and returns the response as a list of
     * Scooter objects wrapped in a ResponseEntity.
     *
     * @return ResponseEntity containing a list of available Scooter objects.
     */
    @GetMapping("/available_scooters/")
    private fun getAvailableScooters(): ResponseEntity<List<Scooter>> {
        return try {
            circuitBreaker.executeSupplier {
                val scooters = scooterRepository.findAll().filter { it.state == "ready" }
                ResponseEntity.ok(scooters)
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emptyList())
        }
    }

    @PutMapping("/set_scooter_state/{scooterId}/")
    fun setScooterState(
        @PathVariable scooterId: String,
        @RequestBody updatedScooter: Scooter,
    ): ResponseEntity<String> {
        return try {
            circuitBreaker.executeSupplier {
                val scooterOptional = scooterRepository.findById(scooterId)
                if (scooterOptional.isPresent) {
                    val existingScooter = scooterOptional.get()
                    existingScooter.state = updatedScooter.state
                    scooterRepository.save(existingScooter)
                    ResponseEntity.ok("Scooter state updated successfully")
                } else {
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body("Scooter not found")
                }
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred")
        }
    }

    // Add this method to ScooterController.kt in maintenance_service
    @PutMapping("/use_scooter/{scooterId}/")
    fun useScooter(@PathVariable scooterId: String): ResponseEntity<String> {
        return try {
            circuitBreaker.executeSupplier {
                val scooterOptional = scooterRepository.findById(scooterId)
                logger.info("Scooter with id $scooterId found")
                if (scooterOptional.isPresent) {
                    val scooter = scooterOptional.get()
                    if (scooter.state == "ready") {
                        scooter.state = "in use"
                        scooterRepository.save(scooter)
                        logger.info("Scooter with id $scooterId is now in use")
                        ResponseEntity.ok("Scooter is now in use")
                    } else {
                        logger.warn("Scooter with id $scooterId is not available")
                        ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Scooter is not available")
                    }
                } else {
                    logger.warn("Scooter with id $scooterId not found")
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body("Scooter not found")
                }
            }
        } catch (e: HttpServerErrorException) {
            logger.error(
                "An error occurred while trying to use scooter with id $scooterId: ${e.message}"
            )
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred")
        }
    }
}
