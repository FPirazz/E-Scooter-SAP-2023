package com.escooter_microservices.escooter.config

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class CircuitBreakerConfiguration {

    @Bean
    fun circuitBreaker(): CircuitBreaker {
        val config = CircuitBreakerConfig.custom()
            .failureRateThreshold(50.0f)
            .waitDurationInOpenState(Duration.ofMillis(1000))
            .slidingWindowSize(2)
            .minimumNumberOfCalls(2)
            .build()

        return CircuitBreaker.of("circuitBreaker", config)
    }
}