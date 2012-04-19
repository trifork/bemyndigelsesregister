package shared

import wslite.soap.SOAPClient

import static dk.bemyndigelsesregister.integrationtest.TestUtil.urlPrefix

abstract class WebServiceSupport {


    protected SOAPClient getClient() {
        println "Creating client for ${urlPrefix()}"
        //def client = new SOAPClient(urlPrefix() + "/bemyndigelsesservice/bemyndigelsesservice.svc")
        def client = new SOAPClient(urlPrefix() + "/bemyndigelsesservice/")
        client.httpClient.proxy = createHttpProxy() ?: Proxy.NO_PROXY
        client
    }

    private Proxy createHttpProxy() {
        if (System.getProperty('http.proxyHost') && System.getProperty('http.proxyPort')) {
            println "Using proxy ${System.getProperty('http.proxyHost')}:${System.getProperty('http.proxyPort')}"
            return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(System.getProperty('http.proxyHost'), Integer.parseInt(System.getProperty('http.proxyPort'))))
        }
    }
}
