package com.escooter_microservices.escooter.repositories

import com.escooter_microservices.escooter.models.Ride
import org.springframework.data.mongodb.repository.MongoRepository

interface RideRepository : MongoRepository<Ride, String>