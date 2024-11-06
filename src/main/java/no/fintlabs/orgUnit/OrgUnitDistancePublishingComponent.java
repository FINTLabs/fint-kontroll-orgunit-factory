package no.fintlabs.orgUnit;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class OrgUnitDistancePublishingComponent {
    private final OrgUnitDistanceService orgUnitDistanceService;


    public OrgUnitDistancePublishingComponent(OrgUnitDistanceService orgUnitDistanceService) {
        this.orgUnitDistanceService = orgUnitDistanceService;
    }

    public void publishOrgUnitDistance() {
        Set<OrgUnitDistance> allOrgUnitDistances = new HashSet<>();

        orgUnitDistanceService.getAllOrgUnits().forEach(orgUnit -> {
            int distance = 0;

            allOrgUnitDistances.add(createSelfOrgUnitDistance(orgUnit));

            //get parent
            //update distance
            //next parent

            while (orgUnitDistanceService.hasParentOrgUnit(orgUnit)) {
                distance++;
                allOrgUnitDistances.add(createOrgUnitDistance(orgUnit,distance));
            }

        });

    }

    public OrgUnitDistance createOrgUnitDistance(OrgUnit orgUnit, int distance) {
        OrgUnitDistance orgUnitDistance = OrgUnitDistance
                .builder()
                .key(orgUnit.getParentRef()+"_"+ orgUnit.getOrganisationUnitId())
                .orgUnitId(orgUnit.getParentRef())
                .subOrgUnitId(orgUnit.getOrganisationUnitId())
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
