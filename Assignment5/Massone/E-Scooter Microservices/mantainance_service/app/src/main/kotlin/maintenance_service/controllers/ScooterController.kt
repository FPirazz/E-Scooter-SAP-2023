package maintenance_service.controllers

import maintenance_service.models.Scooter
import maintenance_service.services.ScooterService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView

@RestController
class ScooterController(private val scooterService: ScooterService) {
    @PostMapping("/create_escooter/")
    fun createScooter(@RequestBody scooter: Scooter): ResponseEntity<Scooter> {
        return ResponseEntity.ok(scooterService.createScooter(scooter))
    }

    @GetMapping("/escooter_created/")
    fun scooterCreated(): ModelAndView {
        return ModelAndView("escooter_created")
    }

    @GetMapping("/all_scooters/")
    fun getAllScooters(): List<Scooter> {
        return scooterService.getAllScooters()
    }

    data class ScooterStateResponse(val state: String)

    @GetMapping("/get_scooter_state/{scooterId}/")
    fun getScooterState(@PathVariable scooterId: String): ResponseEntity<ScooterStateResponse> {
        val scooter = scooterService.getScooterState(scooterId)
        return if (scooter != null) {
            ResponseEntity.ok(ScooterStateResponse(scooter.state ?: "N/A"))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(ScooterStateResponse("error"))
        }
    }

    @GetMapping("/available_scooters/")
    private fun getAvailableScooters(): ResponseEntity<List<Scooter>> {
        val scooters = scooterService.getAvailableScooters()
        return ResponseEntity.ok(scooters)
    }

    @PutMapping("/set_scooter_state/{scooterId}/")
    fun setScooterState(
        @PathVariable scooterId: String,
        @RequestBody updatedScooter: Scooter,
    ): ResponseEntity<String> {
        val scooter = scooterService.setScooterState(scooterId, updatedScooter)
        return if (scooter != null) {
            ResponseEntity.ok("Scooter state updated successfully")
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Scooter not found")
        }
    }

    @PutMapping("/use_scooter/{scooterId}/")
    fun useScooter(@PathVariable scooterId: String): ResponseEntity<String> {
        val scooter = scooterService.useScooter(scooterId)
        return if (scooter != null) {
            if (scooter.state == "in use") {
                ResponseEntity.ok("Scooter is now in use")
            } else {
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Scooter is not available")
            }
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Scooter not found")
        }
    }
}