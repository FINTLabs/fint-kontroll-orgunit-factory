package no.fintlabs.orgUnit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class OrgUnitDistancePublishingComponent {
    private final OrgUnitDistanceService orgUnitDistanceService;
    private final OrgUnitDistanceProducerService orgUnitDistanceProducerService;

    public OrgUnitDistancePublishingComponent(OrgUnitDistanceService orgUnitDistanceService, OrgUnitDistanceProducerService orgUnitDistanceProducerService) {
        this.orgUnitDistanceService = orgUnitDistanceService;
        this.orgUnitDistanceProducerService = orgUnitDistanceProducerService;
    }

    @Scheduled(
            initialDelayString = "${fint.kontroll.orgunitdistance.publishing.initial-delay}",
            fixedDelayString = "${fint.kontroll.orgunitdistance.publishing.fixed-delay}"
    )
    public void publishOrgUnitDistance() {
        log.info("Start publishing orgUnit distance");
        List<OrgUnitDistance> allOrgUnitDistances = new ArrayList<>();

        orgUnitDistanceService.getAllOrgUnits().forEach(orgUnit -> {
            int distance = 0;
            String startOrgUnitId = orgUnit.getOrganisationUnitId();
            OrgUnit currentOrgUnit = orgUnit;
            allOrgUnitDistances.add(orgUnitDistanceService.createOrgUnitDistance(startOrgUnitId,startOrgUnitId,distance));

            while (orgUnitDistanceService.hasParentOrgUnit(currentOrgUnit)) {
                distance++;
                if (orgUnitDistanceService.getParentOrgUnit(currentOrgUnit).isPresent()) {
                    currentOrgUnit = orgUnitDistanceService.getParentOrgUnit(currentOrgUnit).get();

                    allOrgUnitDistances.add(orgUnitDistanceService.createOrgUnitDistance(startOrgUnitId, currentOrgUnit.getOrganisationUnitId(), distance));
                    log.debug("From orgUnitId : {} - to orgUnitId {} - distance: {}", startOrgUnitId, currentOrgUnit.getOrganisationUnitId(), distance);
                }
            }

            log.debug("{} has {} levels of orgunits above", orgUnit.getName(), distance);
        });

        List<OrgUnitDistance> publishedOrgUnitDistances = orgUnitDistanceProducerService.publish(allOrgUnitDistances);
        log.info("Published {} orgUnitDistances", publishedOrgUnitDistances.size());
        log.info("Finished publishing orgUnit distance");
    }
}
