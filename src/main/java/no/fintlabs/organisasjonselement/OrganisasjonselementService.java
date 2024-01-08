package no.fintlabs.organisasjonselement;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.Link;
import no.fint.model.resource.administrasjon.organisasjon.OrganisasjonselementResource;
import no.fintlabs.cache.FintCache;
import no.fintlabs.gyldighetsPeriode.GyldighetsperiodeService;
import no.fintlabs.links.ResourceLinkUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class OrganisasjonselementService {

    private final FintCache<String, OrganisasjonselementResource> organisasjonselementResourceCache;
    private final GyldighetsperiodeService gyldighetsperiodeService;

    public OrganisasjonselementService(FintCache<String, OrganisasjonselementResource> organisasjonselementResourceCache,
                                       GyldighetsperiodeService gyldighetsperiodeService) {
        this.organisasjonselementResourceCache = organisasjonselementResourceCache;
        this.gyldighetsperiodeService = gyldighetsperiodeService;
    }

    public List<OrganisasjonselementResource> getAllValid(Date currentTime) {
        List<OrganisasjonselementResource> allValidOrganisasjonselementer = organisasjonselementResourceCache
                .getAllDistinct()
                .stream()
                .filter(organisasjonselementResource -> gyldighetsperiodeService.isValid(
                        organisasjonselementResource.getGyldighetsperiode(),
                        currentTime
                ))
                .filter(this::hasParent)
                .toList();

        System.out.println("Lengde på cache: " + organisasjonselementResourceCache.getAllDistinct().size());
        return allValidOrganisasjonselementer;

    }

    private boolean hasParent(OrganisasjonselementResource organisasjonselementResource) {
        String overordnaOrganisasjonselementHref = ResourceLinkUtil.getFirstLink(
                organisasjonselementResource::getOverordnet,
                organisasjonselementResource,
                "overordnet"
        );

        return parentIsInCache(overordnaOrganisasjonselementHref);
    }

    private boolean parentIsInCache(String key) {
        String parentId = organisasjonselementResourceCache
                .getOptional(key.toLowerCase())
                .map(organisasjonselementResource -> organisasjonselementResource.getOrganisasjonsId().getIdentifikatorverdi())
                .orElse("Ingen orgID funnet");
        boolean present = organisasjonselementResourceCache.getOptional(key.toLowerCase()).isPresent();
        log.info("Parent orgunit found: " + present);
        return present;
    }


    public List<String> getChildrenOrganisasjonselementUnitResourceOrganisasjonsId(
            OrganisasjonselementResource organisasjonselementResource) {
        String organisasjonsenhetHref = ResourceLinkUtil.getFirstSelfLink(organisasjonselementResource);
        List<String> underordnetOrganisasjonsenhetOrganisasjonsId =
                organisasjonselementResourceCache
                        .get(ResourceLinkUtil.organisasjonsIdToLowerCase(organisasjonsenhetHref))
                        .getUnderordnet()
                        .stream()
                        .map(link -> link.getHref())
                        .map(href -> organisasjonselementResourceCache
                                .get(ResourceLinkUtil.organisasjonsIdToLowerCase(href)))
                        .map(orgunit -> orgunit.getOrganisasjonsId().getIdentifikatorverdi())
                        .toList();
        return underordnetOrganisasjonsenhetOrganisasjonsId;
    }

    public String getParentOrganisasjonselementOrganisasjonsId(
            OrganisasjonselementResource organisasjonselementResource) {
        String parentOrganisasjonselementOrganisasjonsHref = ResourceLinkUtil.getFirstLink(
                organisasjonselementResource::getOverordnet,
                organisasjonselementResource,
                "overordnet"
        );

        return organisasjonselementResourceCache
                .get(parentOrganisasjonselementOrganisasjonsHref.toLowerCase())
                .getOrganisasjonsId()
                .getIdentifikatorverdi();
    }

    public List<String> getAllSubOrgUnitsRefs(OrganisasjonselementResource organisasjonselementResource) {
        List<String> allSubOrgUnitRefs = new ArrayList<>();

        findSubOrgUnitRefs(organisasjonselementResource, allSubOrgUnitRefs);

        return allSubOrgUnitRefs;
    }


    private void findSubOrgUnitRefs(OrganisasjonselementResource organisasjonselementResource,
                                    List<String> allSubOrgUnitRefs) {

        allSubOrgUnitRefs.add(organisasjonselementResource.getOrganisasjonsId().getIdentifikatorverdi());

        findSubOrgUnits(organisasjonselementResource)
                .forEach(orgUnit -> findSubOrgUnitRefs(orgUnit, allSubOrgUnitRefs));
    }

    private List<OrganisasjonselementResource> findSubOrgUnits(OrganisasjonselementResource organisasjonselementResource) {
        String organisasjonsenhetHref = ResourceLinkUtil.getFirstSelfLink(organisasjonselementResource);

        return organisasjonselementResourceCache
                .get(ResourceLinkUtil.organisasjonsIdToLowerCase(organisasjonsenhetHref))
                .getUnderordnet()
                .stream()
                .map(Link::getHref)
                .map(href -> organisasjonselementResourceCache.get(ResourceLinkUtil.organisasjonsIdToLowerCase(href)))
                .toList();
    }
}



