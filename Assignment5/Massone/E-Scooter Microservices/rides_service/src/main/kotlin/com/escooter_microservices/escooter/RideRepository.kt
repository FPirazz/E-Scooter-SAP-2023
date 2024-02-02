package com.escooter_microservices.escooter

import org.springframework.data.mongodb.repository.MongoRepository

interface RideRepository : MongoRepository<Ride, String>