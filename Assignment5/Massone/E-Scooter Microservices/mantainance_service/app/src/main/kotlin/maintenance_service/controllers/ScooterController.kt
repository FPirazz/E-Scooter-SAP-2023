package maintenance_service.controllers

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import maintenance_service.models.Scooter
import maintenance_service.services.ScooterService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.servlet.ModelAndView

@RestController
class ScooterController(
    private val scooterService: ScooterService,
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
                    scooterService.createScooter(scooter)
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
            scooterService.getAllScooters()
        }
    }

    @GetMapping("/get_scooter_state/{scooterId}/")
    fun getScooterState(@PathVariable scooterId: String): ResponseEntity<ScooterStateResponse> {
        return try {
            circuitBreaker.executeSupplier {
                val scooter = scooterService.getScooterState(scooterId)
                if (scooter != null) {
                    ResponseEntity.ok(ScooterStateResponse(scooter.state ?: "N/A"))
                } else {
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body(ScooterStateResponse("error"))
                }
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ScooterStateResponse("error"))
        }
    }

    @GetMapping("/available_scooters/")
    private fun getAvailableScooters(): ResponseEntity<List<Scooter>> {
        return try {
            circuitBreaker.executeSupplier {
                val scooters = scooterService.getAvailableScooters()
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
                val scooter = scooterService.setScooterState(scooterId, updatedScooter)
                if (scooter != null) {
                    ResponseEntity.ok("Scooter state updated successfully")
                } else {
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body("Scooter not found")
                }
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred")
        }
    }

    @PutMapping("/use_scooter/{scooterId}/")
    fun useScooter(@PathVariable scooterId: String): ResponseEntity<String> {
        return try {
            circuitBreaker.executeSupplier {
                val scooter = scooterService.useScooter(scooterId)
                if (scooter != null) {
                    logger.info("Scooter with id $scooterId is now in use")
                    ResponseEntity.ok("Scooter is now in use")
                } else {
                    logger.warn("Scooter with id $scooterId is not available or not found")
                    ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Scooter is not available or not found")
                }
            }
        } catch (e: HttpClientErrorException) {
            logger.error(
                "An error occurred while trying to use scooter with id $scooterId: ${e.message}"
            )
            ResponseEntity.status(e.statusCode).body(e.statusText)
        } catch (e: HttpServerErrorException) {
            logger.error(
                "An error occurred while trying to use scooter with id $scooterId: ${e.message}"
            )
            ResponseEntity.status(e.statusCode).body(e.statusText)
        } catch (e: Exception) {
            logger.error(
                "An unexpected error occurred while trying to use scooter with id $scooterId: ${e.message}"
            )
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred")
        }
    }
}