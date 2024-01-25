package mantainance_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@SpringBootApplication
@EnableWebMvc
class MaintainanceServiceApp

fun main(args: Array<String>) {
    runApplication<MaintainanceServiceApp>(*args)
}