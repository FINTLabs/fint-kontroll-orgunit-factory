package no.fintlabs;

import no.fint.model.resource.administrasjon.organisasjon.OrganisasjonselementResource;
import no.fintlabs.cache.FintCache;
import no.fintlabs.cache.FintCacheManager;
import no.fintlabs.orgUnit.OrgUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

@Configuration
public class EntityCacheConfiguration {
    private final FintCacheManager fintCacheManager;

    public EntityCacheConfiguration(FintCacheManager fintCacheManager) {
        this.fintCacheManager = fintCacheManager;
    }

    @Bean
    FintCache<String, OrganisasjonselementResource> organisasjonselementResourceCache() {
        return createCache(OrganisasjonselementResource.class);
    }

    @Bean
    FintCache<String,OrgUnit> publishedOrgUnitCache() {
        return createCache(OrgUnit.class);
    }

    private <V>FintCache<String, V> createCache( Class<V> resourceClass) {
        return fintCacheManager.createCache(
                resourceClass.getName().toLowerCase(Locale.ROOT),
                String.class,
                resourceClass
        );
    }
}
