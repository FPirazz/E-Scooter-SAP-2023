package maintenance_service.controllers

import maintenance_service.models.Scooter
import maintenance_service.repositories.ScooterRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView


@RestController
class ScooterController(private val scooterRepository: ScooterRepository) {
    @PostMapping("/create_escooter/")
    fun createScooter(@RequestBody scooter: Scooter): ModelAndView {
        if (scooter.name == null || scooter.location == null) {
            return ModelAndView("escooter_error")
        }

        scooterRepository.save(scooter)

        return ModelAndView("escooter_created")
    }

    @GetMapping("/escooter_created/")
    fun scooterCreated(): ModelAndView {
        return ModelAndView("escooter_created")
    }

    @GetMapping("/all_scooters/")
    fun getAllScooters(): List<Scooter> {
        return scooterRepository.findAll()
    }

    data class ScooterStateResponse(val state: String)

    @GetMapping("/get_scooter_state/{scooterId}/")
    fun getScooterState(@PathVariable scooterId: String): ResponseEntity<ScooterStateResponse> {
        val scooterOptional = scooterRepository.findById(scooterId)
        return if (scooterOptional.isPresent) {
            val scooter = scooterOptional.get()
            ResponseEntity.ok(ScooterStateResponse(scooter.state ?: "N/A"))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(ScooterStateResponse("error"))
        }
    }



    @PutMapping("/set_scooter_state/{scooterId}/")
    fun setScooterState(@PathVariable scooterId: String, @RequestBody updatedScooter: Scooter): ResponseEntity<String> {
        val scooterOptional = scooterRepository.findById(scooterId)
        return if (scooterOptional.isPresent) {
            val existingScooter = scooterOptional.get()
            existingScooter.state = updatedScooter.state
            scooterRepository.save(existingScooter)
            ResponseEntity.ok("Scooter state updated successfully")
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Scooter not found")
        }
    }

}