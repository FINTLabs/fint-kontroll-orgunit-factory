package no.fintlabs.orgUnitDistance;

import no.fintlabs.cache.FintCache;
import no.fintlabs.orgUnit.OrgUnit;
import no.fintlabs.orgUnit.OrgUnitDistanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrgUnitDistanceServiceTest {
    @Mock
    private FintCache<String, OrgUnit> orgUnitFintCache;

    OrgUnitDistanceService orgUnitDistanceService;

    private List<OrgUnit> allOrgUnitsFromCache;
    private OrgUnit orgUnitTopLevel;
    private OrgUnit orgUnitSubLevel1;
    private OrgUnit orgUnitSubLevel2;

    @BeforeEach
    public void setUp() {
        allOrgUnitsFromCache = new ArrayList<>();

        orgUnitTopLevel = OrgUnit.builder()
                .organisationUnitId("1")
                .name("Toplevel OrgUnit")
                .parentRef("1")
                .childrenRef(List.of("2", "3"))
                .build();
        allOrgUnitsFromCache.add(orgUnitTopLevel);

        orgUnitSubLevel1 = OrgUnit.builder()
                .organisationUnitId("2")
                .name("Sublevel1 OrgUnit")
                .parentRef("1")
                .childrenRef(List.of("3"))
                .build();
        allOrgUnitsFromCache.add(orgUnitSubLevel1);

        orgUnitSubLevel2 = OrgUnit.builder()
                .organisationUnitId("3")
                .name("Sublevel3 OrgUnit")
                .parentRef("2")
                .childrenRef(null)
                .build();
        allOrgUnitsFromCache.add(orgUnitSubLevel2);
        orgUnitDistanceService = new OrgUnitDistanceService(orgUnitFintCache);
    }

    @Test
    public void shouldReturnTrueforHavingParentOrgUnit() {
        orgUnitDistanceService.hasParentOrgUnit(orgUnitSubLevel1);

        assertTrue(orgUnitDistanceService.hasParentOrgUnit(orgUnitSubLevel1));
    }

    @Test
    public void shouldReturnFalseforHavingParentOrgUnit() {

        assertFalse(orgUnitDistanceService.hasParentOrgUnit(orgUnitTopLevel));
    }

    @Test
    public void shouldReturnParentOrgUnit() {
        when(orgUnitFintCache.getAllDistinct()).thenReturn(allOrgUnitsFromCache);
        OrgUnit parentOrgunit = orgUnitDistanceService.getParentOrgUnit(orgUnitSubLevel2).orElseThrow();
        String parentOrgUnitId = parentOrgunit.getOrganisationUnitId();

        assertEquals(orgUnitSubLevel1.getOrganisationUnitId(), parentOrgUnitId);

    }


    @Test
    void shouldReturnTopLevelOrgUnitAsParentForTopLevelOrgUnit() {
        when(orgUnitFintCache.getAllDistinct()).thenReturn(allOrgUnitsFromCache);
        OrgUnit parentOrgunit = orgUnitDistanceService.getParentOrgUnit(orgUnitTopLevel).orElseThrow();
        String parentTopOrgUnitId = parentOrgunit.getOrganisationUnitId();

        assertEquals(orgUnitTopLevel.getOrganisationUnitId(), parentTopOrgUnitId);
    }
}