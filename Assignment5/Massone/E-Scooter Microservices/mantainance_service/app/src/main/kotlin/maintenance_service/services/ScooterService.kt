package maintenance_service.services

import maintenance_service.models.Scooter
import maintenance_service.repositories.ScooterRepository
import org.springframework.stereotype.Service

@Service
class ScooterService(private val scooterRepository: ScooterRepository) {

    fun createScooter(scooter: Scooter): Scooter {
        return scooterRepository.save(scooter)
    }

    fun getAllScooters(): List<Scooter> {
        return scooterRepository.findAll()
    }

    fun getScooterState(scooterId: String): Scooter? {
        return scooterRepository.findById(scooterId).orElse(null)
    }

    fun getAvailableScooters(): List<Scooter> {
        return scooterRepository.findAll().filter { it.state == "ready" }
    }

    fun setScooterState(scooterId: String, updatedScooter: Scooter): Scooter? {
        val scooterOptional = scooterRepository.findById(scooterId)
        return if (scooterOptional.isPresent) {
            val existingScooter = scooterOptional.get()
            existingScooter.state = updatedScooter.state
            scooterRepository.save(existingScooter)
        } else {
            null
        }
    }

    fun useScooter(scooterId: String): Scooter? {
        val scooterOptional = scooterRepository.findById(scooterId)
        return if (scooterOptional.isPresent) {
            val scooter = scooterOptional.get()
            if (scooter.state == "ready") {
                scooter.state = "in use"
                scooterRepository.save(scooter)
            } else {
                null
            }
        } else {
            null
        }
    }
}