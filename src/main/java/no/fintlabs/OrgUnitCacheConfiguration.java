package no.fintlabs;


import no.fint.model.resource.administrasjon.organisasjon.OrganisasjonselementResource;
import no.fintlabs.cache.FintCache;
import no.fintlabs.cache.FintCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

@Configuration
public class OrgUnitCacheConfiguration {
    private final FintCacheManager fintCacheManager;

    public OrgUnitCacheConfiguration(FintCacheManager fintCacheManager) {
        this.fintCacheManager = fintCacheManager;
    }

    @Bean
    FintCache<String, OrganisasjonselementResource> organisasjonselementResourceCache() {
        return createCache(OrganisasjonselementResource.class);
    }

    private <V>FintCache<String, V> createCache( Class<V> resourceClass) {
        return fintCacheManager.createCache(
                resourceClass.getName().toLowerCase(Locale.ROOT),
                String.class,
                resourceClass
        );
    }
}
