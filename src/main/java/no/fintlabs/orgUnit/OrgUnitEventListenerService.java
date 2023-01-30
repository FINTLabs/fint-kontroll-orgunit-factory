package no.fintlabs.orgUnit;

import no.fint.model.resource.administrasjon.organisasjon.OrganisasjonselementResource;
import no.fintlabs.cache.FintCache;
import no.fintlabs.cache.FintCacheEvent;
import no.fintlabs.cache.ehcache.FintEhCacheEventListener;
import no.fintlabs.links.ResourceLinkUtil;
import org.springframework.stereotype.Service;

@Service("orgUnitEventListenerService")
public class OrgUnitEventListenerService {
    private final FintCache<String , OrganisasjonselementResource> organisasjonselementResourceCache;
    private final OrgUnitService orgUnitService;

    public OrgUnitEventListenerService(
            FintCache<String, OrganisasjonselementResource> organisasjonselementResourceCache, 
            OrgUnitService orgUnitService) {
        this.organisasjonselementResourceCache = organisasjonselementResourceCache;
        this.orgUnitService = orgUnitService;
        organisasjonselementResourceCache.addEventListener(new FintEhCacheEventListener<>() {
            public void onEvent(FintCacheEvent<String, OrganisasjonselementResource> fintCacheEvent) {
                onOrgUnitEvent(fintCacheEvent);
                
            }
        });
    }

    private void onOrgUnitEvent(FintCacheEvent<String, OrganisasjonselementResource> fintCacheEvent) {
        OrganisasjonselementResource organisasjonselementResource = fintCacheEvent.getNewValue();
        String overordnetOrganisasjonselementResourceHref = ResourceLinkUtil.getFirstLink(
             organisasjonselementResource::getOverordnet,
             organisasjonselementResource,
             "Overordnet"
        );

        organisasjonselementResourceCache.getOptional(overordnetOrganisasjonselementResourceHref)
                .ifPresent(overorndaOrganisasjonselementResource -> orgUnitService.create(
                        organisasjonselementResource
                ));
    }
}
