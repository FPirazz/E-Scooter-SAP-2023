package mantainance_service

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView

@Controller
class Controller {
    @GetMapping("/")
    fun home(): ModelAndView {
        return ModelAndView("maintainers")
    }
}