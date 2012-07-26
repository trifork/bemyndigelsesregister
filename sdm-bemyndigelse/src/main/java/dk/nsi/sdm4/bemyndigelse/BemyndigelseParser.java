package dk.nsi.sdm4.bemyndigelse;

import dk.nsi.sdm2.core.parser.SimpleParser;
import dk.nsi.sdm4.bemyndigelse.domain.Bemyndigelse;
import dk.nsi.sdm4.bemyndigelse.domain.BemyndigelseRecord;
import dk.nsi.sdm4.bemyndigelse.domain.Bemyndigelser;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class BemyndigelseParser extends SimpleParser<BemyndigelseRecord, Bemyndigelser, Bemyndigelse> {
    @Override
    protected Collection<Bemyndigelse> getContainedEntitiesFrom(Bemyndigelser type) {
        return type.getBemyndigelseList();
    }

    @Override
    public BemyndigelseRecord transform(Bemyndigelse bemyndigelse) {
        return BemyndigelseRecord.createFrom(bemyndigelse);
    }
}
