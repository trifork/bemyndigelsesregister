package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import dk.bemyndigelsesregister.bemyndigelsesservice.server.BemyndigelsesExportJob;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.io.IOException;

@Controller
@RequestMapping("/op")
public class OperationsController {
    private static Logger logger = Logger.getLogger(OperationsController.class);

    @Inject
    BemyndigelsesExportJob exportJob;

    @RequestMapping("/export")
    @ResponseBody
    public String startCompleteExport() {
        try {
            exportJob.completeExport();
        } catch (IOException e) {
            logger.error("Could run complete export", e);
            return "Could not complete with reason: " + e.getMessage();
        }
        return "DONE";
    }
}