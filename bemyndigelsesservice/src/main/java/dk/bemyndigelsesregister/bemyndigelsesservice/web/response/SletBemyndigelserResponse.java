package dk.bemyndigelsesregister.bemyndigelsesservice.web.response;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "SletBemyndigelserDao", namespace = "http://web.bemyndigelsesservice.bemyndigelsesregister.dk/")
public class SletBemyndigelserResponse {
    @XmlElement(name = "SlettedeBemyndigelsesKoder", required = true)
    protected List<String> slettedeBemyndigelsesKoder;

    public List<String> getSlettedeBemyndigelsesKoder() {
        return slettedeBemyndigelsesKoder;
    }

    public void setSlettedeBemyndigelsesKoder(List<String> slettedeBemyndigelsesKoder) {
        this.slettedeBemyndigelsesKoder = slettedeBemyndigelsesKoder;
    }
}
