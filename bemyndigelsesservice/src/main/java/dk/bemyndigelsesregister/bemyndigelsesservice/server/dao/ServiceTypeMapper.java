package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Arbejdsfunktion;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegerbarRettighed;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Rettighed;
import dk.nsi.bemyndigelse._2012._05._01.Arbejdsfunktioner;
import dk.nsi.bemyndigelse._2012._05._01.DelegerbarRettigheder;
import dk.nsi.bemyndigelse._2012._05._01.Rettigheder;

import java.util.Collection;

public interface ServiceTypeMapper {
    Arbejdsfunktioner toJaxbArbejdsfunktioner(Collection<Arbejdsfunktion> arbejdsfunktionList);

    Rettigheder toJaxbRettigheder(Collection<Rettighed> rettighedList);

    DelegerbarRettigheder toJaxbDelegerbarRettigheder(Collection<DelegerbarRettighed> delegerbarRettighedList);


    // ------ 2016.01.01 ------

    dk.nsi.bemyndigelse._2016._01._01.Delegation toDelegationType(Delegation delegation);
}
