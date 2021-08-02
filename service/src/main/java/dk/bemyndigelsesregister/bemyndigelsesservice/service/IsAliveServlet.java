/*
 * The MIT License
 *
 * Original work sponsored and donated by The Danish Health Data Authority (http://www.sundhedsdatastyrelsen.dk)
 *
 * Copyright (C) 2018 The Danish Health Data Authority (http://www.sundhedsdatastyrelsen.dk)
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dk.bemyndigelsesregister.bemyndigelsesservice.service;

import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.time.Instant;
import java.util.Properties;

@WebServlet("/isalive")
public class IsAliveServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(IsAliveServlet.class);

    private static BuildData buildData;

    private static final String CONTENT_TYPE = "text/plain";
    private static final String CR = "\n";

    /*
        private static final List<StatusReporter> statusReporters = new LinkedList<>();

        public static void addStatusReporter(StatusReporter reporter) {
            statusReporters.add(reporter);
        }
    */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

        try {
            BuildData bd = getBuildData(request.getSession().getServletContext());

            StringBuilder s = new StringBuilder();
            int status = HttpURLConnection.HTTP_OK;
            response.setContentType(CONTENT_TYPE);
            s.append("Current time: ").append(Instant.now()).append(CR);
//            s.append("Service start time: ").append(AppConfig.startTime).append(CR);
            s.append("Build-Date: ").append(bd.builtDate).append(CR);
            s.append("Build-Version: ").append(bd.builtVersion).append(CR);

            /*
            for (StatusReporter reporter : statusReporters) {
                IsAliveStatus reportStatus = reporter.reportStatus();
                if (reportStatus.getStatusCode() > status) {
                    status = reportStatus.getStatusCode();
                }
                s.append(CR).append(reporter.getReporterName()).append(':').append(CR).append(reportStatus.getStatusMessage()).append(CR);
            }
*/
            response.getWriter().println("Status: " + status);
            response.getWriter().println(s.toString());
            response.setHeader("X-SERVICE-VERSION", bd.builtVersion);
            response.setStatus(status);
        } catch (Exception ex) {
            response.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
            log.error("Error in isAlive", ex);
        }
    }

    private BuildData getBuildData(ServletContext loader) throws IOException {
        if (buildData == null) {
            InputStream stream = loader.getResourceAsStream("/META-INF/MANIFEST.MF");
            if (stream != null) {
                Properties prop = new Properties();
                prop.load(stream);
                stream.close();
                buildData = new BuildData(prop.getProperty("Built-Version"), prop.getProperty("Built-Date"));

                log.info("Got build data: " + buildData);
            } else {
                throw new RuntimeException("Manifelt not found");
            }
        }
        return buildData;
    }
}

class BuildData {
    String builtVersion;
    String builtDate;

    public BuildData(String builtVersion, String builtDate) {
        this.builtVersion = builtVersion;
        this.builtDate = builtDate;
    }

    @Override
    public String toString() {
        return "BuildData{" +
                "builtVersion='" + builtVersion + '\'' +
                ", builtDate='" + builtDate + '\'' +
                '}';
    }
}

