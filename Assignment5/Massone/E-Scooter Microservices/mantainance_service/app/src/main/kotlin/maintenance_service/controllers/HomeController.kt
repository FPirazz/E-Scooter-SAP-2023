package maintenance_service.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView

/**
 * Controller for the maintainance service.
 */
@Controller
class HomeController {
    @GetMapping("/")
    fun home(): ModelAndView {
        return ModelAndView("maintenance")
    }
}