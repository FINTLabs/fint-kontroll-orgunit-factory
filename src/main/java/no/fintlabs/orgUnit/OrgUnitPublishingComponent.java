package no.fintlabs.orgUnit;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.administrasjon.organisasjon.OrganisasjonselementResource;
import no.fintlabs.links.ResourceLinkUtil;
import no.fintlabs.organisasjonselement.OrganisasjonselementService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class OrgUnitPublishingComponent {
    private final OrganisasjonselementService organisasjonselementService;
    private final OrgUnitEntityProducerService orgUnitEntityProducerService;

    public OrgUnitPublishingComponent(OrganisasjonselementService organisasjonselementService, OrgUnitEntityProducerService orgUnitEntityProducerService) {
        this.organisasjonselementService = organisasjonselementService;
        this.orgUnitEntityProducerService = orgUnitEntityProducerService;
    }

    @Scheduled(
            initialDelayString = "${fint.kontroll.orgunit.publishing.initial-delay}",
            fixedDelayString = "${fint.kontroll.orgunit.publishing.fixed-delay}"
    )
    public void publishOrgUnits(){
        Date currentTime = Date.from(Instant.now());

        List<OrgUnit> validOrgUnits = organisasjonselementService.getAllValid(currentTime)
                .stream()
                .map(this::createOrgUnit)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        orgUnitEntityProducerService.publish(validOrgUnits);
    }

    private Optional<OrgUnit> createOrgUnit( OrganisasjonselementResource organisasjonselementResource){
        return Optional.of(
                OrgUnit
                .builder()
                .resourceId(ResourceLinkUtil.getFirstSelfLink(organisasjonselementResource))
                .organisationUnitId(organisasjonselementResource.getOrganisasjonsId().getIdentifikatorverdi())
                .name(organisasjonselementResource.getNavn())
                .shortName(organisasjonselementResource.getKortnavn())
                .parentRef(organisasjonselementService
                        .getParentOrganisasjonselementOrganisasjonsId(organisasjonselementResource))
                .childrenRef(organisasjonselementService
                        .getChildrenOrganisasjonselementUnitResourceOrganisasjonsId(organisasjonselementResource))
                .managerRef(organisasjonselementResource.getLeder().toString())
                .build());
    }




}
