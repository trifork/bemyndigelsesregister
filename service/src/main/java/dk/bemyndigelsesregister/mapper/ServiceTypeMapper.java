package dk.bemyndigelsesregister.mapper;

import dk.bemyndigelsesregister.domain.Delegation;

public interface ServiceTypeMapper {
    dk.nsi.bemyndigelse._2017._08._01.Delegation toDelegationType(Delegation delegation);
}
