package com.escooter_microservices.escooter.services

import com.escooter_microservices.escooter.models.Ride
import com.escooter_microservices.escooter.repositories.RideRepository
import org.springframework.stereotype.Service

@Service
class RideService(private val rideRepository: RideRepository) {

    fun createRide(ride: Ride): Ride {
        return rideRepository.save(ride)
    }

    fun getRides(): List<Ride> {
        return rideRepository.findAll()
    }

    fun removeRide(id: String) {
        rideRepository.deleteById(id)
    }
}