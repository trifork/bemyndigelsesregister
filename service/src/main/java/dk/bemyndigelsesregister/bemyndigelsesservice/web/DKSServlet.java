package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import org.apache.log4j.Logger;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

@Component
@RequestMapping
public class DKSServlet {
    private static final Logger logger = Logger.getLogger(DKSServlet.class);
    private static final String RESOURCE_NAME = "/dksconfig/BEM.xml";

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(DateTimeZone.UTC);

    private static Integer localPort = null;

    @Value("${dcc.endpoint}")
    private String dccEndpoint;

    @RequestMapping(value = {"/dksconfig", "/bem/dksconfig"})
    @ResponseBody
    public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String env = req.getParameter("Env");
        logger.debug("dksconfig request, Env=" + env);

        if (localPort == null) {
            localPort = req.getServerPort();
        }

        URL url = getClass().getResource(RESOURCE_NAME);

        String filetimestamp = DATETIME_FORMATTER.print(url.openConnection().getLastModified());

        InputStream in = getClass().getResourceAsStream(RESOURCE_NAME);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
        }

        reader.close();
        in.close();

        res.setHeader("Content-Type", "application/xml");
        res.setHeader("Cache-Control", "no-cache");
        res.getWriter().write(getStringWithResolvedVariables(out, filetimestamp));

        try {
            HttpSession session = req.getSession(false);
            if (session != null)
                session.invalidate();
        } catch (Exception e) {
            logger.error("Could not invalidate session after dksconfig request!", e);
        }

        return null;
    }

    private String getStringWithResolvedVariables(StringBuilder template, String filetimestamp) {
        int timestampIndex = template.indexOf("TIMESTAMP");
        template = template.replace(timestampIndex, timestampIndex + 9, filetimestamp);

        return template.toString().replace("ENDPOINT_SERVERNAME_PLACEHOLDER", dccEndpoint);
    }
}
