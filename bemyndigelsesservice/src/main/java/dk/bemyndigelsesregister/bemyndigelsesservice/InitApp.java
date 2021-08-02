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
package dk.bemyndigelsesregister.bemyndigelsesservice;

import dk.bemyndigelsesregister.bemyndigelsesservice.utils.Log4JConfigurator;
import dk.sdsd.nsp.slalog.ws.SLALoggingServletFilter;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.EnumSet;

@WebListener
public class InitApp implements ServletContextListener {
    private static final String SHORT_APP_NAME = "bem";

    // Configure Log4J
    static {
        Log4JConfigurator.configure(SHORT_APP_NAME);
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // Configure SLA Logging filter
        SLALoggingServletFilter slaLoggingServletFilter = new SLALoggingServletFilter();
        FilterRegistration.Dynamic dynamic = servletContextEvent.getServletContext().addFilter("slaLoggingFilter", slaLoggingServletFilter);
        dynamic.setInitParameter("shortAppName", SHORT_APP_NAME); // makes nsp-util look for "nspslalog-bem.properties"
        EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
        dynamic.addMappingForUrlPatterns(dispatcherTypes, false, "/*");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
