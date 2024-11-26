package no.fintlabs.organisasjonselement;

import no.fint.model.felles.kompleksedatatyper.Identifikator;
import no.fint.model.resource.Link;
import no.fint.model.resource.administrasjon.organisasjon.OrganisasjonselementResource;
import no.fintlabs.cache.FintCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrganisasjonselementServiceTest {
    @Mock
    private FintCache<String,OrganisasjonselementResource> organisasjonselementResourceCache;

    @Test
    public void shouldGetAllSubOrgUnitsRefs() {
        OrganisasjonselementService organisasjonselementService = new OrganisasjonselementService(organisasjonselementResourceCache, null);

        OrganisasjonselementResource parent = new OrganisasjonselementResource();

        Identifikator organisasjonsIdParent = new Identifikator();
        String parentId = "1";
        organisasjonsIdParent.setIdentifikatorverdi(parentId);
        parent.setOrganisasjonsId(organisasjonsIdParent);

        Map<String, List<Link>> parentLinks = new HashMap<>();
        String parentLink = "https://beta.felleskomponent.no/administrasjon/organisasjon/organisasjonselement/organisasjonsid/1";
        parentLinks.put("self", List.of(new Link(parentLink)));
        parent.setLinks(parentLinks);

        String firstLayerSubOrgUnitLink = "https://beta.felleskomponent.no/administrasjon/organisasjon/organisasjonselement/organisasjonsid/5";
        Link firstLayerSubOrgUnitSelfLink = new Link(firstLayerSubOrgUnitLink);
        parent.addUnderordnet(firstLayerSubOrgUnitSelfLink);

        OrganisasjonselementResource firstLayerSubOrgUnit = new OrganisasjonselementResource();

        Identifikator firstLayerSubOrgUnitIdentifikator = new Identifikator();
        String firstLayerSubOrgUnitId = "5";
        firstLayerSubOrgUnitIdentifikator.setIdentifikatorverdi(firstLayerSubOrgUnitId);
        firstLayerSubOrgUnit.setOrganisasjonsId(firstLayerSubOrgUnitIdentifikator);

        Map<String, List<Link>> firstLayerSubOrgUnitLinks = new HashMap<>();
        firstLayerSubOrgUnitLinks.put("self", List.of(firstLayerSubOrgUnitSelfLink));

        firstLayerSubOrgUnit.setLinks(firstLayerSubOrgUnitLinks);

        String secondLayerSubOrgUnitLink = "https://beta.felleskomponent.no/administrasjon/organisasjon/organisasjonselement/organisasjonsid/26";
        Link secondLayerSubOrgUnitSelfLink = new Link(secondLayerSubOrgUnitLink);
        firstLayerSubOrgUnit.addUnderordnet(secondLayerSubOrgUnitSelfLink);

        OrganisasjonselementResource secondLayerSubOrgUnit = new OrganisasjonselementResource();

        Identifikator secondLayerSubOrgUniIdentifikator = new Identifikator();
        String secondLayerSubOrgUnitId = "26";
        secondLayerSubOrgUniIdentifikator.setIdentifikatorverdi(secondLayerSubOrgUnitId);
        secondLayerSubOrgUnit.setOrganisasjonsId(secondLayerSubOrgUniIdentifikator);

        HashMap<String, List<Link>> secondLayerSubOrgUnitLinks = new HashMap<>();
        secondLayerSubOrgUnitLinks.put("self", List.of(secondLayerSubOrgUnitSelfLink));

        secondLayerSubOrgUnit.setLinks(secondLayerSubOrgUnitLinks);

        when(organisasjonselementResourceCache.get(parentLink)).thenReturn(parent);
        when(organisasjonselementResourceCache.get(firstLayerSubOrgUnitLink)).thenReturn(firstLayerSubOrgUnit);
        when(organisasjonselementResourceCache.get(secondLayerSubOrgUnitLink)).thenReturn(secondLayerSubOrgUnit);

        List<String> allSubOrgUnitsRefs = organisasjonselementService.getAllSubOrgUnitsRefs(parent);

        assertThat(allSubOrgUnitsRefs).hasSize(3);
        assertThat(allSubOrgUnitsRefs).contains(parentId);
        assertThat(allSubOrgUnitsRefs).contains(firstLayerSubOrgUnitId);
        assertThat(allSubOrgUnitsRefs).contains(secondLayerSubOrgUnitId);

    }
}
