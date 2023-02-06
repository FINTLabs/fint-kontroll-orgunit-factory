package no.fintlabs.organisasjonselement;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.administrasjon.organisasjon.OrganisasjonselementResource;
import no.fintlabs.cache.FintCache;
import no.fintlabs.gyldighetsPeriode.GyldighetsperiodeService;
import no.fintlabs.links.ResourceLinkUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class OrganisasjonselementService {
    private final FintCache<String,OrganisasjonselementResource> organisasjonselementResourceCache;
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
                .filter(organisasjonselementResource -> hasParent(organisasjonselementResource))
                .toList();
        return allValidOrganisasjonselementer;

    }

    private boolean hasParent(OrganisasjonselementResource organisasjonselementResource){
        String overordnaOrganisasjonselementHref = ResourceLinkUtil.getFirstLink(
                organisasjonselementResource::getOverordnet,
                organisasjonselementResource,
                "overordnet"
        );

        return parentIsInCache(overordnaOrganisasjonselementHref);
    }

    private boolean parentIsInCache(String key){
        return organisasjonselementResourceCache.getOptional(key).isPresent();
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
                .get(parentOrganisasjonselementOrganisasjonsHref)
                .getOrganisasjonsId()
                .getIdentifikatorverdi();
    }
}



