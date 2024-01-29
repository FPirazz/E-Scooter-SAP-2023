package maintenance_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@SpringBootApplication
@EnableWebMvc
class MaintenanceServiceApp

fun main(args: Array<String>) {
    runApplication<MaintenanceServiceApp>(*args)
}