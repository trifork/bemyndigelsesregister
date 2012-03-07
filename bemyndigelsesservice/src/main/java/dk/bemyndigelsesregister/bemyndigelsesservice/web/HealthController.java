package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HealthController {
    @RequestMapping("/health")
    public @ResponseBody String health() {
        return "OK";
    }
}
