package maintenance_service.controllers

import maintenance_service.models.Scooter
import maintenance_service.repositories.ScooterRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/create_escooter/")
class ScooterController(private val scooterRepository: ScooterRepository) {

    @PostMapping
    fun createScooter(@RequestBody scooter: Scooter): ResponseEntity<out Any> {
        if (scooter.name == null || scooter.location == null) {
            return ResponseEntity("Missing request parameters", HttpStatus.BAD_REQUEST)
        }

        val savedScooter = scooterRepository.save(scooter)
        return ResponseEntity(savedScooter, HttpStatus.CREATED)
    }
}