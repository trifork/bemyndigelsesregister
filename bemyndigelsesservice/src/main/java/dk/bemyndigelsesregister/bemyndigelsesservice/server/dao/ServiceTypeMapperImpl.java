package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Arbejdsfunktion;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegerbarRettighed;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Rettighed;
import dk.nsi.bemyndigelse._2012._05._01.Arbejdsfunktioner;
import dk.nsi.bemyndigelse._2012._05._01.DelegerbarRettigheder;
import dk.nsi.bemyndigelse._2012._05._01.Rettigheder;
import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class ServiceTypeMapperImpl implements ServiceTypeMapper {
    @Override
    public Arbejdsfunktioner toJaxbArbejdsfunktioner(final Collection<Arbejdsfunktion> arbejdsfunktionList) {
        return new Arbejdsfunktioner() {{
            getArbejdsfunktion().addAll(CollectionUtils.collect(
                    arbejdsfunktionList,
                    new Transformer<dk.bemyndigelsesregister.bemyndigelsesservice.domain.Arbejdsfunktion, Arbejdsfunktion>() {
                        @Override
                        public Arbejdsfunktion transform(final dk.bemyndigelsesregister.bemyndigelsesservice.domain.Arbejdsfunktion that) {
                            return new Arbejdsfunktion() {{
                                this.setArbejdsfunktion(that.getArbejdsfunktion());
                                this.setBeskrivelse(that.getBeskrivelse());
                                this.setDomaene(that.getDomaene().getDomaene());
                                this.setSystem(that.getLinkedSystem().getSystem());
                            }};
                        }
                    }
            ));
        }};
    }

    @Override
    public Rettigheder toJaxbRettigheder(final Collection<Rettighed> rettighedList) {
        return new Rettigheder() {{
            getRettighed().addAll(CollectionUtils.collect(
                    rettighedList,
                    new Transformer<dk.bemyndigelsesregister.bemyndigelsesservice.domain.Rettighed, Rettighed>() {
                        @Override
                        public Rettighed transform(final dk.bemyndigelsesregister.bemyndigelsesservice.domain.Rettighed that) {
                            return new Rettighed() {{
                                this.setBeskrivelse(that.getBeskrivelse());
                                this.setDomaene(that.getDomaene().getDomaene());
                                this.setRettighed(that.getRettighedskode());
                                this.setSystem(that.getLinkedSystem().getSystem());
                            }};
                        }
                    }
            ));
        }};
    }

    @Override
    public DelegerbarRettigheder toJaxbDelegerbarRettigheder(final Collection<DelegerbarRettighed> delegerbarRettighedList) {
        return new DelegerbarRettigheder() {{
            getDelegerbarRettighed().addAll(CollectionUtils.collect(
                    delegerbarRettighedList,
                    new Transformer<dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegerbarRettighed, DelegerbarRettighed>() {
                        @Override
                        public DelegerbarRettighed transform(final dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegerbarRettighed that) {
                            return new DelegerbarRettighed() {{
                                this.setArbejdsfunktion(that.getArbejdsfunktion().getArbejdsfunktion());
                                this.setDomaene(that.getDomaene().getDomaene());
                                this.setRettighed(that.getKode());
                                this.setSystem(that.getSystem().getSystem());
                            }};
                        }
                    }
            ));
        }};
    }
}
