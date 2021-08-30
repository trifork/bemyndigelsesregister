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
package dk.bemyndigelsesregister.bemyndigelsesservice.utils;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import java.io.File;
import java.net.URL;

public class Log4JConfigurator {

	public static void configure(String module) {
		boolean configured = false;
		String serverConfigDirProperty = System.getProperty("jboss.server.config.dir");
		if (serverConfigDirProperty != null) {
			File log4jConfigFile = new File(new File(serverConfigDirProperty), "log4j-" + module + ".xml");
			if (log4jConfigFile.isFile() && log4jConfigFile.canRead()) {
				try {
					DOMConfigurator.configure(log4jConfigFile.getCanonicalPath());
					configured = true;
					Logger log = Logger.getLogger(Log4JConfigurator.class);
					log.info("Log4J configured from Wildfly configuration directory: " + log4jConfigFile.getCanonicalPath());
				} catch (Exception e) {
					throw new IllegalStateException("Could not configue Log4J from " + log4jConfigFile.getName(), e);
				}
			}
		}

		if(!configured && serverConfigDirProperty != null) {
				File log4jConfigFile = new File(new File(serverConfigDirProperty), "log4j-" + module + ".properties");
				if (log4jConfigFile.isFile() && log4jConfigFile.canRead()) {
					try {
						PropertyConfigurator.configure(log4jConfigFile.getAbsolutePath());
						configured = true;
						Logger log = Logger.getLogger(Log4JConfigurator.class);
						log.info("Log4J configured from Wildfly configuration directory: " + log4jConfigFile.getCanonicalPath());
					} catch (Exception e) {
						throw new IllegalStateException("Could not configue Log4J from " + log4jConfigFile.getName(), e);
					}				
			}
		}

		if (!configured) {
			URL log4jConfigURL = Log4JConfigurator.class.getClassLoader().getResource("log4j-" + module + ".xml");
			if (log4jConfigURL != null) {
				try {
					DOMConfigurator.configure(log4jConfigURL);
					configured = true;
					Logger log = Logger.getLogger(Log4JConfigurator.class);
					log.info("Log4J configured from classpath: " + log4jConfigURL.toExternalForm());
				} catch (Exception e) {
					throw new IllegalStateException("Could not configue Log4J from " + log4jConfigURL.toExternalForm(), e);
				}
			}
		}

		if (!configured) {
			URL log4jConfigURL = Log4JConfigurator.class.getClassLoader().getResource("log4j-" + module + ".properties");
			if (log4jConfigURL != null) {
				try {
					PropertyConfigurator.configure(log4jConfigURL);
					configured = true;
					Logger log = Logger.getLogger(Log4JConfigurator.class);
					log.info("Log4J configured from classpath: " + log4jConfigURL.toExternalForm());
				} catch (Exception e) {
					throw new IllegalStateException("Could not configue Log4J from " + log4jConfigURL.toExternalForm(), e);
				}
			}
		}

		if (!configured) {
			URL log4jConfigURL = Log4JConfigurator.class.getClassLoader().getResource("log4j.xml");
			if (log4jConfigURL != null) {
				try {
					DOMConfigurator.configure(log4jConfigURL);
					configured = true;
					Logger log = Logger.getLogger(Log4JConfigurator.class);
					log.info("Log4J configured from classpath: " + log4jConfigURL.toExternalForm());
				} catch (Exception e) {
					throw new IllegalStateException("Could not configue Log4J from " + log4jConfigURL.toExternalForm(), e);
				}
			}
		}
		
		if (!configured) {
			URL log4jConfigURL = Log4JConfigurator.class.getClassLoader().getResource("log4j.properties");
			if (log4jConfigURL != null) {
				try {
					PropertyConfigurator.configure(log4jConfigURL);
					configured = true;
					Logger log = Logger.getLogger(Log4JConfigurator.class);
					log.info("Log4J configured from classpath: " + log4jConfigURL.toExternalForm());
				} catch (Exception e) {
					throw new IllegalStateException("Could not configue Log4J from " + log4jConfigURL.toExternalForm(), e);
				}
			}
		}

		if (!configured) {
			File log4jConfigFile = new File("log4j-" + module + ".xml");
			if (log4jConfigFile.isFile() && log4jConfigFile.canRead()) {
				try {
					DOMConfigurator.configure(log4jConfigFile.getCanonicalPath());
					configured = true;
					Logger log = Logger.getLogger(Log4JConfigurator.class);
					log.info("Log4J configured from local file: " + log4jConfigFile.getCanonicalPath());
				} catch (Exception e) {
					throw new IllegalStateException("Could not configue Log4J from " + log4jConfigFile.getName(), e);
				}
			}
		}
		
		if (!configured) {
			File log4jConfigFile = new File("log4j-" + module + ".properties");
			if (log4jConfigFile.isFile() && log4jConfigFile.canRead()) {
				try {
					PropertyConfigurator.configure(log4jConfigFile.getCanonicalPath());
					configured = true;
					Logger log = Logger.getLogger(Log4JConfigurator.class);
					log.info("Log4J configured from local file: " + log4jConfigFile.getCanonicalPath());
				} catch (Exception e) {
					throw new IllegalStateException("Could not configue Log4J from " + log4jConfigFile.getName(), e);
				}
			}
		}

		if (!configured) {
			throw new IllegalStateException("Could not configure Log4J for module " + module);
		}
	}

}
