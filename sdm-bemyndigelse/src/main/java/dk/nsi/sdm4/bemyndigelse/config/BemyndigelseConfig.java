package dk.nsi.sdm4.bemyndigelse.config;

import dk.nsi.sdm2.core.annotations.EnableStamdata;
import dk.nsi.sdm2.core.config.StamdataConfigurationSupport;
import dk.nsi.sdm2.core.parser.Parser;
import dk.nsi.sdm4.bemyndigelse.BemyndigelseParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@EnableStamdata(home = "bemyndigelse")
@ComponentScan("dk.nsi.sdm4.bemyndigelse")
public class BemyndigelseConfig implements StamdataConfigurationSupport {
    @Override
    public Parser parser() {
        return new BemyndigelseParser();
    }

    @Bean(name = {"nspMarshaller", "nspUnarshaller"})
    public Jaxb2Marshaller marshaller() {
        final Jaxb2Marshaller bean = new Jaxb2Marshaller();
        bean.setContextPath(
                "dk.nsi.bemyndigelser._2012._04"
        );
        return bean;
    }

}
