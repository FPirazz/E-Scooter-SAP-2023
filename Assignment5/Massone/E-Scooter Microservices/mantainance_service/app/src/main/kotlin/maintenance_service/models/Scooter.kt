package maintenance_service.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "scooters")
data class Scooter(
    @Id
    val id: String? = null,
    val name: String? = null,
    val location: String? = null
)