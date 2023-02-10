package no.fintlabs.organisasjonselement;

import no.fint.model.resource.FintLinks;
import no.fint.model.resource.administrasjon.organisasjon.OrganisasjonselementResource;
import no.fintlabs.cache.FintCache;
import no.fintlabs.kafka.entity.EntityConsumerFactoryService;
import no.fintlabs.kafka.entity.topic.EntityTopicNameParameters;
import no.fintlabs.links.ResourceLinkUtil;
import no.fintlabs.orgUnit.OrgUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

@Configuration
public class OrganisasjonselementConsumerConfiguration {
    private final EntityConsumerFactoryService entityConsumerFactoryService;

    public OrganisasjonselementConsumerConfiguration(EntityConsumerFactoryService entityConsumerFactoryService) {
        this.entityConsumerFactoryService = entityConsumerFactoryService;
    }


    private <T extends FintLinks> ConcurrentMessageListenerContainer<String, T> createCacheConsumer(
            String resourceReference,
            Class<T> resourceClass,
            FintCache<String, T> cache
    ) {
        return entityConsumerFactoryService.createFactory(
                resourceClass,
                consumerRecord -> cache.put(
                        ResourceLinkUtil.getSelfLinks(consumerRecord.value()),
                        consumerRecord.value()
                )
        ).createContainer(EntityTopicNameParameters.builder().resource(resourceReference).build());
    }


    @Bean
    ConcurrentMessageListenerContainer<String,OrganisasjonselementResource> organisasjonselementResourceConsumer(
            FintCache<String,OrganisasjonselementResource> organisasjonselementResourceCache){
        return createCacheConsumer(
                "administrasjon.organisasjon.organisasjonselement",
                OrganisasjonselementResource.class,
                organisasjonselementResourceCache);
    }

    @Bean
    ConcurrentMessageListenerContainer<String, OrgUnit> orgUnitEntityConsumer(
            FintCache<String, OrgUnit> publishedOrgUnitCache){
        return entityConsumerFactoryService.createFactory(
                OrgUnit.class,
                consumerRecord -> {
                    OrgUnit orgUnit = consumerRecord.value();
                    publishedOrgUnitCache.put(
                            orgUnit.getResourceId(),
                            orgUnit
                    );
                }
                ).createContainer(EntityTopicNameParameters.builder().resource("orgunit").build());
    }
}
