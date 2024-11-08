package no.fintlabs.orgUnit;


import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.boot.availability.AvailabilityChangeEvent.publish;

//get parent
//update distance
//next parent


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
            initialDelayString = "50000",
            fixedDelayString = "30000"
    )
    public void publishOrgUnitDistance() {
        List<OrgUnitDistance> allOrgUnitDistances =new ArrayList<>();

        orgUnitDistanceService.getAllOrgUnits().forEach(orgUnit -> {
            int distance = 0;
            allOrgUnitDistances.add(createSelfOrgUnitDistance(orgUnit));

            String startOrgUnitId = orgUnit.getOrganisationUnitId();

            OrgUnit currentOrgUnit = orgUnit;

            while (orgUnitDistanceService.hasParentOrgUnit(currentOrgUnit)) {
                distance++;
                if (orgUnitDistanceService.getParentOrgUnit(currentOrgUnit).isPresent()){
                    currentOrgUnit = orgUnitDistanceService.getParentOrgUnit(currentOrgUnit).get();

                    allOrgUnitDistances.add(createOrgUnitDistance(startOrgUnitId, currentOrgUnit.getOrganisationUnitId(), distance));
                    log.info("From orgUnitId : {} - to orgUnitId {} - distance: {}", startOrgUnitId, currentOrgUnit.getOrganisationUnitId(), distance );

                } else {
                    log.info("{} has {} levels of orgunits above", orgUnit.getName(), distance);
                }
            }

        });

        orgUnitDistanceProducerService.publish(allOrgUnitDistances);



    }

    public OrgUnitDistance createOrgUnitDistance(String startOrgUnitId,String currentOrgUnitId, int distance) {
        OrgUnitDistance orgUnitDistance = OrgUnitDistance
                .builder()
                .key(currentOrgUnitId+"_"+ startOrgUnitId)
                .orgUnitId(currentOrgUnitId)
                .subOrgUnitId(startOrgUnitId)
                .distance(distance)
                .build();

        return orgUnitDistance;
    }


    public OrgUnitDistance createSelfOrgUnitDistance(OrgUnit orgUnit) {
        return OrgUnitDistance
                .builder()
                .key(orgUnit.getOrganisationUnitId()+"_"+ orgUnit.getOrganisationUnitId())
                .orgUnitId(orgUnit.getOrganisationUnitId())
                .subOrgUnitId(orgUnit.getOrganisationUnitId())
                .distance(0)
                .build();
    }




}
