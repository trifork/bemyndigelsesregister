package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import dk.bemyndigelsesregister.bemyndigelsesservice.server.SystemService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.inject.Inject;

@Controller
public class HealthController {
    private static Logger logger = Logger.getLogger(HealthController.class);
    @Inject
    SystemService systemService;

    @Value("${github.home}")
    String githubHome;

    @RequestMapping("/health")
    public @ResponseBody
    String health() {
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
        final String url = githubHome + "/commits/" + version();
        logger.debug("Redirecting user to url=" + url);
        return new ModelAndView(new RedirectView(url));
    }
}
