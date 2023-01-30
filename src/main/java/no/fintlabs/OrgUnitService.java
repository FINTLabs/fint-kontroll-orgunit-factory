package no.fintlabs;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.administrasjon.organisasjon.OrganisasjonselementResource;
import no.fintlabs.cache.FintCache;
import no.fintlabs.links.ResourceLinkUtil;
import no.fintlabs.orgUnit.OrgUnit;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class OrgUnitService {
    private final FintCache<String, OrganisasjonselementResource> organisasjonselementResourceCache;

    public OrgUnitService(FintCache<String, OrganisasjonselementResource> organisasjonselementResourceCache) {
        this.organisasjonselementResourceCache = organisasjonselementResourceCache;
    }


    public OrgUnit create(OrganisasjonselementResource organisasjonselementResource){
        return OrgUnit
                .builder()
                .organisationUnitId(organisasjonselementResource.getOrganisasjonsId().getIdentifikatorverdi())
                .name(organisasjonselementResource.getNavn())
                .shortName(organisasjonselementResource.getKortnavn())
                .parentRef(getParentOrgUnitResource(organisasjonselementResource)
                        .getOrganisasjonsId()
                        .getIdentifikatorverdi())
                .childrenRef(getChildrenOrgUnitResourceOrganisasjonsId(organisasjonselementResource))
                .managerRef(organisasjonselementResource.getLeder().toString())
                .build();
    }


    private List<String> getChildrenOrgUnitResourceOrganisasjonsId(
            OrganisasjonselementResource organisasjonselementResource) {
        String orgUnitSelfLinkHref = ResourceLinkUtil.getFirstSelfLink(organisasjonselementResource);

        return organisasjonselementResourceCache.get(ResourceLinkUtil.systemIdToLowerCase(orgUnitSelfLinkHref))
                .getUnderordnet()
                .stream()
                .map(selfLink -> selfLink.getHref())
                .map(href -> organisasjonselementResourceCache.getOptional(ResourceLinkUtil.systemIdToLowerCase(href)))
                .map(childOrgUnit -> childOrgUnit.get().getOrganisasjonsId().getIdentifikatorverdi())
                .toList();
    }


    private OrganisasjonselementResource getParentOrgUnitResource(
            OrganisasjonselementResource organisasjonselementResource){
        String overordnaOrganisasjonselementHref = ResourceLinkUtil.getFirstLink(
                organisasjonselementResource::getOverordnet,
                organisasjonselementResource,
                "overordnet"
        );
        return organisasjonselementResourceCache.get(overordnaOrganisasjonselementHref);
    }
}
