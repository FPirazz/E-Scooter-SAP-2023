package maintenance_service.controllers

import maintenance_service.models.Scooter
import maintenance_service.repositories.ScooterRepository
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView

@Controller
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
    fun getScooters(): ModelAndView {
        return ModelAndView("escooter_created")
    }
}