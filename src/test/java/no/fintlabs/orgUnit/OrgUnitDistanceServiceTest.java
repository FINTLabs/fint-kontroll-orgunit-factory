package no.fintlabs.orgUnit;

import no.fintlabs.cache.FintCache;
import org.junit.jupiter.api.BeforeAll;
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
    private FintCache<String,OrgUnit> orgUnitFintCache;



    @Test
    public void shouldReturnTrueforHavingParentOrgUnit() {
        List<OrgUnit> allOrgUnitsFromCache = new ArrayList<>();
        OrgUnit orgUnitTopLevel = OrgUnit.builder()
                .organisationUnitId("1")
                .name("Toplevel OrgUnit")
                .parentRef("1")
                .childrenRef(List.of("2","3"))
                .build();
        OrgUnit orgUnitSubLevel1 = OrgUnit.builder()
                .organisationUnitId("2")
                .name("Sublevel1 OrgUnit")
                .parentRef("1")
                .childrenRef(List.of("3"))
                .build();
        OrgUnit orgUnitSubLevel2 = OrgUnit.builder()
                .organisationUnitId("3")
                .name("Sublevel3 OrgUnit")
                .parentRef("2")
                .childrenRef(null)
                .build();
        OrgUnitDistanceService orgUnitDistanceService = new OrgUnitDistanceService(orgUnitFintCache);
        orgUnitDistanceService.hasParentOrgUnit(orgUnitSubLevel1);

        assertTrue(orgUnitDistanceService.hasParentOrgUnit(orgUnitSubLevel1));

    }

    @Test
    public void shouldReturnFalseforHavingParentOrgUnit() {
        OrgUnit orgUnitTopLevel = OrgUnit.builder()
                .organisationUnitId("1")
                .name("Toplevel OrgUnit")
                .parentRef("1")
                .childrenRef(List.of("2","3"))
                .build();

        OrgUnitDistanceService orgUnitDistanceService = new OrgUnitDistanceService(orgUnitFintCache);

        assertFalse(orgUnitDistanceService.hasParentOrgUnit(orgUnitTopLevel));
    }

    @Test
    public void shouldReturnParentOrgUnit() {
        List<OrgUnit> allOrgUnitsFromCache = new ArrayList<>();
        OrgUnit orgUnitTopLevel = OrgUnit.builder()
                .organisationUnitId("1")
                .name("Toplevel OrgUnit")
                .parentRef("1")
                .childrenRef(List.of("2","3"))
                .build();
        allOrgUnitsFromCache.add(orgUnitTopLevel);
        OrgUnit orgUnitSubLevel1 = OrgUnit.builder()
                .organisationUnitId("2")
                .name("Sublevel1 OrgUnit")
                .parentRef("1")
                .childrenRef(List.of("3"))
                .build();
        allOrgUnitsFromCache.add(orgUnitSubLevel1);
        OrgUnit orgUnitSubLevel2 = OrgUnit.builder()
                .organisationUnitId("3")
                .name("Sublevel3 OrgUnit")
                .parentRef("2")
                .childrenRef(null)
                .build();
        allOrgUnitsFromCache.add(orgUnitSubLevel2);
        OrgUnitDistanceService orgUnitDistanceService = new OrgUnitDistanceService(orgUnitFintCache);

        when(orgUnitFintCache.getAllDistinct()).thenReturn(allOrgUnitsFromCache);

        OrgUnit parentOrgunit = orgUnitDistanceService.getParentOrgUnit(orgUnitSubLevel2).orElseThrow();
        String parentOrgUnitId = parentOrgunit.getOrganisationUnitId();

        assertEquals(orgUnitSubLevel1.getOrganisationUnitId(), parentOrgUnitId);

    }


    @Test void shouldReturnTopLevelOrgUnitAsParentForTopLevelOrgUnit() {
        List<OrgUnit> allOrgUnitsFromCache = new ArrayList<>();
        OrgUnit orgUnitTopLevel = OrgUnit.builder()
                .organisationUnitId("1")
                .name("Toplevel OrgUnit")
                .parentRef("1")
                .childrenRef(List.of("2","3"))
                .build();
        allOrgUnitsFromCache.add(orgUnitTopLevel);
        OrgUnit orgUnitSubLevel1 = OrgUnit.builder()
                .organisationUnitId("2")
                .name("Sublevel1 OrgUnit")
                .parentRef("1")
                .childrenRef(List.of("3"))
                .build();
        allOrgUnitsFromCache.add(orgUnitSubLevel1);
        OrgUnit orgUnitSubLevel2 = OrgUnit.builder()
                .organisationUnitId("3")
                .name("Sublevel3 OrgUnit")
                .parentRef("2")
                .childrenRef(null)
                .build();
        allOrgUnitsFromCache.add(orgUnitSubLevel2);
        OrgUnitDistanceService orgUnitDistanceService = new OrgUnitDistanceService(orgUnitFintCache);

        when(orgUnitFintCache.getAllDistinct()).thenReturn(allOrgUnitsFromCache);

        OrgUnit parentOrgunit = orgUnitDistanceService.getParentOrgUnit(orgUnitTopLevel).orElseThrow();
        String parentTopOrgUnitId = parentOrgunit.getOrganisationUnitId();

        assertEquals(orgUnitTopLevel.getOrganisationUnitId(), parentTopOrgUnitId);
    }


}