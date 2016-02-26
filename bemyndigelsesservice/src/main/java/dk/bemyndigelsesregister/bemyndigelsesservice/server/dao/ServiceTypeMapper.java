package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;

public interface ServiceTypeMapper {
    dk.nsi.bemyndigelse._2016._01._01.Delegation toDelegationType(Delegation delegation);
}
