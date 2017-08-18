package dk.bemyndigelsesregister.shared.web;

import org.apache.log4j.Logger;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Created by obj on 05-07-2016.
 */
@Component
@RequestMapping
public class DKSServlet {
    private static final Logger logger = Logger.getLogger(DKSServlet.class);
    private static final String RESOURCE_NAME = "/dksconfig/BEM.xml";

    private static final String TEST1_ENV = "bemtest.ddv.netic.dk:8080";
    private static final String TEST2_ENV = "bemtest.ddv.netic.dk:8080";
    private static final String UDD_ENV = "bemtest.ddv.netic.dk:8080";
    private static final String PRODTEST_ENV = "bemtest.ddv.netic.dk:8080";
    private static final String PROD_ENV = "bemprod.netic.dsdn.dk:8080";

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(DateTimeZone.UTC);

    private static Integer localPort = null;

    @RequestMapping("/dksconfig")
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
        res.getWriter().write(getStringWithResolvedVariables(out, env, filetimestamp));

        try {
            HttpSession session = req.getSession(false);
            if (session != null)
                session.invalidate();
        } catch (Exception e) {
            logger.error("Could not invalidate session after dksconfig request!", e);
        }

        return null;
    }

    private String getStringWithResolvedVariables(StringBuilder template, String env, String filetimestamp) {
        int timestampIndex = template.indexOf("TIMESTAMP");
        template = template.replace(timestampIndex, timestampIndex + 9, filetimestamp);

        if (env == null || env.isEmpty()) {
            return template.toString().replace("ENDPOINT_SERVERNAME_PLACEHOLDER", getEndpointFromLocalServer());
        } else if (env.toLowerCase().equals("prod")) {
            return template.toString().replace("ENDPOINT_SERVERNAME_PLACEHOLDER", PROD_ENV);
        } else if (env.toLowerCase().equals("test1")) {
            return template.toString().replace("ENDPOINT_SERVERNAME_PLACEHOLDER", TEST1_ENV);
        } else if (env.toLowerCase().equals("test2")) {
            return template.toString().replace("ENDPOINT_SERVERNAME_PLACEHOLDER", TEST2_ENV);
        } else if (env.toLowerCase().equals("udd")) {
            return template.toString().replace("ENDPOINT_SERVERNAME_PLACEHOLDER", UDD_ENV);
        } else if (env.toLowerCase().equals("prodtest")) {
            return template.toString().replace("ENDPOINT_SERVERNAME_PLACEHOLDER", PRODTEST_ENV);
        } else {
            return template.toString();
        }
    }

    /* The method strips the name of the local server so the name of the individual appserver is not shown.
        Example: app01.test1.fmk.netic.dk -> test1.fmk.netic.dk i.e. the name known outside
     */
    private String getEndpointFromLocalServer() {
        try {
            String hostName = InetAddress.getLocalHost().getHostName();

            if (hostName.toLowerCase().contains("test1")) {
                return TEST1_ENV;
            } else if (hostName.toLowerCase().contains("test2")) {
                return TEST2_ENV;
            } else if (hostName.toLowerCase().contains("udd")) {
                return UDD_ENV;
            } else if (hostName.toLowerCase().contains("prodtest")) {
                return PRODTEST_ENV;
            } else if (hostName.toLowerCase().contains("bemprod")) {
                return PROD_ENV;
            } else {
                return hostName + ":" + localPort;
            }
        } catch (UnknownHostException e) {
            logger.warn("Unable to resolve local server name");
        }
        return "UNKNOWN HOST";
    }
}
