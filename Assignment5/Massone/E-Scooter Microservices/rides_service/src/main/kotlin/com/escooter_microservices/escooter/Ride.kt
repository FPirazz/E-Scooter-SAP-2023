package com.escooter_microservices.escooter

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "rides")
class Ride(
    @Id
    var id: String? = null,
    var startLocation: String,
    var endLocation: String,
    var startTime: LocalDateTime,
    var endTime: LocalDateTime
)