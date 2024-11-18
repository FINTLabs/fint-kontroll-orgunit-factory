package no.fintlabs.orgUnit;

import no.fint.model.felles.kompleksedatatyper.Identifikator;
import no.fint.model.resource.Link;
import no.fint.model.resource.administrasjon.organisasjon.OrganisasjonselementResource;
import no.fintlabs.orgUnit.OrgUnit;
import no.fintlabs.orgUnit.OrgUnitEntityProducerService;
import no.fintlabs.orgUnit.OrgUnitPublishingComponent;
import no.fintlabs.organisasjonselement.OrganisasjonselementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrgUnitPublishingComponentTest {

    @Mock
    private OrganisasjonselementService organisasjonselementService;

    @Mock
    private OrgUnitEntityProducerService orgUnitEntityProducerService;

    @Test
    public void shouldCreateOrgUnit() {

        OrganisasjonselementResource organisasjonselementResource = new OrganisasjonselementResource();
        Identifikator organisasjonsId = new Identifikator();
        String organisasjonsIdVerdi = "1";
        organisasjonsId.setIdentifikatorverdi(organisasjonsIdVerdi);
        organisasjonselementResource.setOrganisasjonsId(organisasjonsId);

        Map<String, List<Link>> links = new HashMap<>();
        String link = "https://beta.felleskomponent.no/administrasjon/organisasjon/organisasjonselement/organisasjonsid/1";
        links.put("self", List.of(new Link(link)));
        organisasjonselementResource.setLinks(links);

        when(organisasjonselementService.getParentOrganisasjonselementOrganisasjonsId(organisasjonselementResource)).thenReturn("");
        when(organisasjonselementService.getChildrenOrganisasjonselementUnitResourceOrganisasjonsId(organisasjonselementResource))
                .thenReturn(List.of("2", "3", "4"));
        when(organisasjonselementService.getAllSubOrgUnitsRefs(organisasjonselementResource)).thenReturn(
                List.of("1", "2", "3", "4", "5", "6"));

        Optional<OrgUnit> orgUnitFound = new OrgUnitPublishingComponent(organisasjonselementService, orgUnitEntityProducerService)
                .createOrgUnit(organisasjonselementResource);

        assertThat(orgUnitFound).isPresent();

        OrgUnit orgUnit = orgUnitFound.get();

        assertThat(orgUnit.getOrganisationUnitId()).isEqualTo("1");
        assertThat(orgUnit.getParentRef()).isEmpty();
        assertThat(orgUnit.getChildrenRef()).containsExactlyInAnyOrder("2", "3", "4");
        assertThat(orgUnit.getAllSubOrgUnitsRef()).containsExactlyInAnyOrder("1", "2", "3", "4", "5", "6");
    }
}
