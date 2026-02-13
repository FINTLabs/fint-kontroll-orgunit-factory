package no.fintlabs.organisasjonselement;

import no.novari.kafka.consuming.ErrorHandlerConfiguration;
import no.novari.kafka.consuming.ErrorHandlerFactory;
import no.novari.kafka.consuming.ListenerConfiguration;
import no.novari.kafka.consuming.ParameterizedListenerContainerFactoryService;
import no.novari.kafka.topic.name.EntityTopicNameParameters;
import no.novari.kafka.topic.name.TopicNamePrefixParameters;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.FintLinks;
import no.fint.model.resource.administrasjon.organisasjon.OrganisasjonselementResource;
import no.fintlabs.cache.FintCache;
import no.fintlabs.links.ResourceLinkUtil;
import no.fintlabs.orgUnit.OrgUnit;
import no.fintlabs.orgUnit.OrgUnitDistance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
public class OrganisasjonselementConsumerConfiguration {
    private final ParameterizedListenerContainerFactoryService parameterizedListenerContainerFactoryService;
    private final ErrorHandlerFactory errorHandlerFactory;

    public OrganisasjonselementConsumerConfiguration(
            ParameterizedListenerContainerFactoryService parameterizedListenerContainerFactoryService,
            ErrorHandlerFactory errorHandlerFactory) {
        this.parameterizedListenerContainerFactoryService = parameterizedListenerContainerFactoryService;
        this.errorHandlerFactory = errorHandlerFactory;
    }


    private <T extends FintLinks> ConcurrentMessageListenerContainer<String, T> createCacheConsumer(
            String resourceReference,
            Class<T> resourceClass,
            FintCache<String, T> cache
    ) {
        ListenerConfiguration listenerConfiguration = ListenerConfiguration
                .stepBuilder()
                .groupIdApplicationDefault()
                .maxPollRecordsKafkaDefault()
                .maxPollIntervalKafkaDefault()
                .seekToBeginningOnAssignment()
                .build();

        return parameterizedListenerContainerFactoryService.createRecordListenerContainerFactory(
                resourceClass,
                consumerRecord -> {
                    T value = consumerRecord.value();
                    cache.put(ResourceLinkUtil.getSelfLinks(consumerRecord.value()), consumerRecord.value());
                },
                listenerConfiguration,
                errorHandlerFactory.createErrorHandler(ErrorHandlerConfiguration
                        .stepBuilder()
                        .noRetries()
                        .skipFailedRecords().build())
        ).createContainer(EntityTopicNameParameters
                .builder()
                .topicNamePrefixParameters(
                        TopicNamePrefixParameters
                                .stepBuilder()
                                .orgIdApplicationDefault()
                                .domainContextApplicationDefault()
                                .build()
                )
                .resourceName(resourceReference).build());
    }


    @Bean
    ConcurrentMessageListenerContainer<String, OrganisasjonselementResource> organisasjonselementResourceConsumer(
            FintCache<String, OrganisasjonselementResource> organisasjonselementResourceCache) {
        return createCacheConsumer(
                "administrasjon-organisasjon-organisasjonselement",
                OrganisasjonselementResource.class,
                organisasjonselementResourceCache);
    }

    @Bean
    ConcurrentMessageListenerContainer<String, OrgUnit> orgUnitEntityConsumer(
            FintCache<String, OrgUnit> publishedOrgUnitCache) {

        ListenerConfiguration listenerConfiguration = ListenerConfiguration
                .stepBuilder()
                .groupIdApplicationDefault()
                .maxPollRecordsKafkaDefault()
                .maxPollIntervalKafkaDefault()
                .seekToBeginningOnAssignment()
                .build();

        return parameterizedListenerContainerFactoryService.createRecordListenerContainerFactory(
                OrgUnit.class,
                consumerRecord -> {
                    OrgUnit orgUnit = consumerRecord.value();
                    publishedOrgUnitCache.put(
                            orgUnit.getResourceId(),
                            orgUnit);
                },
                listenerConfiguration,
                errorHandlerFactory.createErrorHandler(ErrorHandlerConfiguration
                        .stepBuilder()
                        .noRetries()
                        .skipFailedRecords()
                        .build())
        ).createContainer(
                EntityTopicNameParameters.builder()
                        .topicNamePrefixParameters(TopicNamePrefixParameters.stepBuilder()
                                .orgIdApplicationDefault()
                                .domainContextApplicationDefault()
                                .build())
                        .resourceName("orgunit")
                        .build());
    }

    @Bean
    ConcurrentMessageListenerContainer<String, OrgUnitDistance> orgUnitDistanceEntityConsumer(
            FintCache<String, OrgUnitDistance> orgUnitDistanceCache) {

        ListenerConfiguration listenerConfiguration = ListenerConfiguration
                .stepBuilder()
                .groupIdApplicationDefault()
                .maxPollRecordsKafkaDefault()
                .maxPollIntervalKafkaDefault()
                .seekToBeginningOnAssignment()
                .build();


        return parameterizedListenerContainerFactoryService.createRecordListenerContainerFactory(
                OrgUnitDistance.class,
                consumerRecord -> {
                    OrgUnitDistance orgUnitDistance = consumerRecord.value();
                    orgUnitDistanceCache.put(
                            orgUnitDistance.getId(),
                            orgUnitDistance);
                    log.debug("Added {} to cache", orgUnitDistance.getId());
                },
                listenerConfiguration,
                errorHandlerFactory.createErrorHandler(ErrorHandlerConfiguration
                        .stepBuilder()
                        .noRetries()
                        .skipFailedRecords()
                        .build())
        ).createContainer(
                EntityTopicNameParameters.builder()
                        .topicNamePrefixParameters(TopicNamePrefixParameters.stepBuilder()
                                .orgIdApplicationDefault()
                                .domainContextApplicationDefault()
                                .build())
                        .resourceName("orgunitdistance")
                        .build());
    }
}
