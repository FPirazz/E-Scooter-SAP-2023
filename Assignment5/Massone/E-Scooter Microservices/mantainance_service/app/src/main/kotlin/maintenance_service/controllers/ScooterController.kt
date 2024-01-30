package maintenance_service.controllers

import maintenance_service.models.Scooter
import maintenance_service.repositories.ScooterRepository
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/create_escooter/")
class ScooterController(private val scooterRepository: ScooterRepository) {
    @PostMapping
    fun createScooter(@RequestBody scooter: Scooter): ResponseEntity<String> {
        if (scooter.name == null || scooter.location == null) {
            return ResponseEntity("Missing request parameters", HttpStatus.BAD_REQUEST)
        }

        scooterRepository.save(scooter)

        val headers = HttpHeaders()
        headers.contentType = MediaType.TEXT_HTML
        return ResponseEntity("<html><body><p>Escooter created</p></body></html>", headers, HttpStatus.CREATED)
    }
}