package no.fintlabs.orgUnit;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.administrasjon.organisasjon.OrganisasjonselementResource;
import no.fintlabs.cache.FintCache;
import no.fintlabs.links.ResourceLinkUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrgUnitService {
    private final FintCache<String, OrganisasjonselementResource> organisasjonselementResourceCache;
    private final OrgUnitEntityProducerService orgUnitEntityProducerService;

    public OrgUnitService(
            FintCache<String, OrganisasjonselementResource> organisasjonselementResourceCache,
            OrgUnitEntityProducerService orgUnitEntityProducerService) {
        this.organisasjonselementResourceCache = organisasjonselementResourceCache;
        this.orgUnitEntityProducerService = orgUnitEntityProducerService;
    }


    public OrgUnit create(OrganisasjonselementResource organisasjonselementResource){
        OrgUnit newOrUpdatedOrgunit = OrgUnit
                .builder()
                .resourceId(ResourceLinkUtil.getFirstSelfLink(organisasjonselementResource))
                .organisationUnitId(organisasjonselementResource.getOrganisasjonsId().getIdentifikatorverdi())
                .name(organisasjonselementResource.getNavn())
                .shortName(organisasjonselementResource.getKortnavn())
                .parentRef(getParentOrgUnitResource(organisasjonselementResource)
                        .getOrganisasjonsId()
                        .getIdentifikatorverdi())
                .childrenRef(getChildrenOrgUnitResourceOrganisasjonsId(organisasjonselementResource))
                .managerRef(organisasjonselementResource.getLeder().toString())
                .build();
        orgUnitEntityProducerService.publish(newOrUpdatedOrgunit);
        return newOrUpdatedOrgunit;
    }


    private List<String> getChildrenOrgUnitResourceOrganisasjonsId(
            OrganisasjonselementResource organisasjonselementResource) {
        String orgUnitSelfLinkHref = ResourceLinkUtil.getFirstSelfLink(organisasjonselementResource);

        List<String> subOrgUnitList = organisasjonselementResourceCache.get(ResourceLinkUtil.organisasjonsIdToLowerCase(orgUnitSelfLinkHref))
                .getUnderordnet()
                .stream()
                .map(selfLink -> selfLink.getHref())
                .map(href -> organisasjonselementResourceCache.get(ResourceLinkUtil.organisasjonsIdToLowerCase(href)))
                .map(childOrgUnit -> childOrgUnit.getOrganisasjonsId().getIdentifikatorverdi())
                .collect(Collectors.toList());

        return subOrgUnitList;
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
