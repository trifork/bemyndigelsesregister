package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegerbarRettighed;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Domaene;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.LinkedSystem;

import java.util.List;

public interface DelegerbarRettighedDao {
    List<DelegerbarRettighed> findBy(Domaene domaene, LinkedSystem linkedSystem);
}
