package dk.bemyndigelsesregister.bemyndigelsesservice.web.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SletBemyndigelserRequest", namespace = "http://web.bemyndigelsesservice.bemyndigelsesregister.dk/")
public class SletBemyndigelserRequest {
    @XmlElement(name = "BemyndigelsesKode", required = true)
    protected List<String> bemyndigelsesKoder;

    public List<String> getBemyndigelsesKoder() {
        return bemyndigelsesKoder;
    }

    public void setBemyndigelsesKoder(List<String> bemyndigelsesKoder) {
        this.bemyndigelsesKoder = bemyndigelsesKoder;
    }
}
