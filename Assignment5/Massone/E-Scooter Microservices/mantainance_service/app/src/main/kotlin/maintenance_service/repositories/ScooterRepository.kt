package maintenance_service.repositories

import maintenance_service.models.Scooter
import org.springframework.data.mongodb.repository.MongoRepository

interface ScooterRepository : MongoRepository<Scooter, String>