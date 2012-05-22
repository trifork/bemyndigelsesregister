package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegerbarRettighed;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Domaene;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.LinkedSystem;

import java.util.List;

public interface DelegerbarRettighedDao {
    DelegerbarRettighed get(long id);

    void save(DelegerbarRettighed delegerbarRettighed);

    DelegerbarRettighed findByKode(String kode);

    List<DelegerbarRettighed> findBy(Domaene domaene, LinkedSystem linkedSystem);
}
