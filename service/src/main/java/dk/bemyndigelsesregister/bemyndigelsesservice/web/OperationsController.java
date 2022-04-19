package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import dk.bemyndigelsesregister.bemyndigelsesservice.server.DelegationExportJob;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;

@Controller
@RequestMapping("/op")
public class OperationsController {
    private static Logger logger = Logger.getLogger(OperationsController.class);

    @Inject
    DelegationExportJob exportJob;

    @RequestMapping("/export")
    @ResponseBody
    public String startCompleteExport() {
        exportJob.completeExport();
        return "DONE";
    }
}
