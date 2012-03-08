package dk.bemyndigelsesregister.shared.web;

import dk.bemyndigelsesregister.shared.service.SystemService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.inject.Inject;

@Controller
public class HealthController {
    @Inject
    SystemService systemService;

    @RequestMapping("/health")
    public @ResponseBody String health() {
        return "OK";
    }

    @RequestMapping("/health/version")
    @ResponseBody
    public String version() {
        return systemService.getImplementationBuild();
    }

    @RequestMapping("/health/home")
    @ResponseBody
    public ModelAndView versionHome() {
        String ghHome = "https://www.github.com/trifork/bemyndigelsesregister";
        return new ModelAndView(new RedirectView(ghHome + "/commits/" + version()));
    }
}
