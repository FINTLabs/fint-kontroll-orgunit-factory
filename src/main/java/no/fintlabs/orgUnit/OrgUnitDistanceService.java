package no.fintlabs.orgUnit;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.cache.FintCache;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class OrgUnitDistanceService {

    private final FintCache<String,OrgUnit> orgUnitCache;

    public OrgUnitDistanceService(FintCache<String, OrgUnit> orgUnitCache) {
        this.orgUnitCache = orgUnitCache;
    }

    public int findDistance(String orgUnit, String orgUnitDistance) {
        return 0;
    }

    public String findToplevelOrgUnit(Set<OrgUnit> orgUnits) {
        return null;
    }

    public List<OrgUnit> getAllOrgUnits() {
        List<OrgUnit> orgUnits = orgUnitCache.getAllDistinct();
        log.info("Number of distinct orgunits in cache: {}", orgUnits.size());

        return orgUnits;
    }

    public boolean hasParentOrgUnit(OrgUnit orgUnit) {
        return !orgUnit.getOrganisationUnitId().equals(orgUnit.getParentRef());
    }


    public Optional<OrgUnit> getParentOrgUnit(OrgUnit orgUnit) {

        return orgUnitCache.getAllDistinct().stream()
                .filter(orgUnitAbove -> orgUnitAbove.getOrganisationUnitId().equals(orgUnit.getParentRef()))
                .findFirst();
    }

}
