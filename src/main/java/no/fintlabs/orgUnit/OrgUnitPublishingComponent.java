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

    public OrgUnitPublishingComponent(OrganisasjonselementService organisasjonselementService,
                                      OrgUnitEntityProducerService orgUnitEntityProducerService) {
        this.organisasjonselementService = organisasjonselementService;
        this.orgUnitEntityProducerService = orgUnitEntityProducerService;
    }

    @Scheduled(
            initialDelayString = "${fint.kontroll.orgunit.publishing.initial-delay}",
            fixedDelayString = "${fint.kontroll.orgunit.publishing.fixed-delay}"
    )
    public void publishOrgUnits() {
        Date currentTime = Date.from(Instant.now());


        List<OrgUnit> validOrgUnits = organisasjonselementService.getAllValid(currentTime)
                .stream()
                .peek(organisasjonselementResource -> log.info("Valid organisasjonselement: {}", organisasjonselementResource.getOrganisasjonsId().getIdentifikatorverdi()))
                .map(this::createOrgUnit)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .peek(orgUnit -> log.info("Oppretta orgunit: {}", orgUnit.getOrganisationUnitId()))
                .toList();

        List<OrgUnit> publishedOrgUnits = orgUnitEntityProducerService.publish(validOrgUnits);
        log.info("Published {} og {} valid orgUnits", publishedOrgUnits.size(), validOrgUnits.size());
    }

    Optional<OrgUnit> createOrgUnit(OrganisasjonselementResource organisasjonselementResource) {
        String parentOrganisasjonselementOrganisasjonsId =
                organisasjonselementService.getParentOrganisasjonselementOrganisasjonsId(organisasjonselementResource);

        List<String> childrenOrganisasjonselementUnitResourceOrganisasjonsId =
                organisasjonselementService.getChildrenOrganisasjonselementUnitResourceOrganisasjonsId(organisasjonselementResource);

        List<String> allSubOrgUnitRefs = organisasjonselementService.getAllSubOrgUnitsRefs(organisasjonselementResource);

        log.info("resourceID: {}", ResourceLinkUtil.getFirstSelfLink(organisasjonselementResource));
        log.info("orgUnitId: {}", organisasjonselementResource.getOrganisasjonsId().getIdentifikatorverdi());
        log.info("name: {}", organisasjonselementResource.getNavn());
        log.info("shortName: {}", organisasjonselementResource.getKortnavn());
        log.info("parentRef: {}", parentOrganisasjonselementOrganisasjonsId);
        log.info("childrernRef: {}", childrenOrganisasjonselementUnitResourceOrganisasjonsId);
        log.info("managerRef: {}", organisasjonselementResource.getLeder().toString());
        log.info("subOrgUnitRefs: {}", allSubOrgUnitRefs);

        return Optional.of(
                OrgUnit.builder()
                        .resourceId(ResourceLinkUtil.getFirstSelfLink(organisasjonselementResource))
                        .organisationUnitId(organisasjonselementResource.getOrganisasjonsId().getIdentifikatorverdi())
                        .name(organisasjonselementResource.getNavn())
                        .shortName(organisasjonselementResource.getKortnavn())
                        .parentRef(parentOrganisasjonselementOrganisasjonsId)
                        .childrenRef(childrenOrganisasjonselementUnitResourceOrganisasjonsId)
                        .managerRef(organisasjonselementResource.getLeder().toString())
                        .allSubOrgUnitsRef(allSubOrgUnitRefs)
                        .build());
    }


}
