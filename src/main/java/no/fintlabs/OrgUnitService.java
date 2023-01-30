package no.fintlabs;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.administrasjon.organisasjon.OrganisasjonselementResource;
import no.fintlabs.orgUnit.OrgUnit;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrgUnitService {
    public void process(OrganisasjonselementResource value) {
    }

    public OrgUnit create(OrganisasjonselementResource organisasjonselementResource){
        OrgUnit orgUnit = OrgUnit
                .builder()
                .organisationUnitId(organisasjonselementResource.getOrganisasjonsId().getIdentifikatorverdi())
                .name(organisasjonselementResource.getNavn())
                .shortName(organisasjonselementResource.getKortnavn())
                .parentRef(getParentOrgUnitResource(organisasjonselementResource).getOrganisasjonsId().getIdentifikatorverdi())
                .build();
    }


    private OrganisasjonselementResource getParentOrgUnitResource(OrganisasjonselementResource organisasjonselementResource){
        String

    }
}
