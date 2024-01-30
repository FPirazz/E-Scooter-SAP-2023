package maintenance_service.repositories

import maintenance_service.models.Scooter
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Repository

@Repository
class ScooterRepository(private val mongoTemplate: MongoTemplate) {

    fun save(scooter: Scooter): Scooter {
        mongoTemplate.save(scooter, "scooters")
        return scooter
    }
}