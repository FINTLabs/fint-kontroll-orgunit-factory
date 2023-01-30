package no.fintlabs;

import no.fint.model.resource.administrasjon.organisasjon.OrganisasjonselementResource;
import no.fintlabs.kafka.entity.EntityConsumerFactoryService;
import no.fintlabs.kafka.entity.topic.EntityTopicNameParameters;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.function.Consumer;

@Configuration
public class OrgUnitConsumerConfiguration {

    public ConcurrentMessageListenerContainer<String, OrganisasjonselementResource> organisasjonselementResourceConsumer(
            OrgUnitService orgUnitService,
            EntityConsumerFactoryService entityConsumerFactoryService
    ){
        EntityTopicNameParameters entityTopicNameParameters = EntityTopicNameParameters
                .builder()
                .resource("administrasjon.organisasjon.organisasjonselement")
                .build();
        ConcurrentMessageListenerContainer consumer = entityConsumerFactoryService.createFactory(
                OrganisasjonselementResource.class,
                (ConsumerRecord<String, OrganisasjonselementResource> consumerRecord)
                -> orgUnitService.process(consumerRecord.value()))
                .createContainer(entityTopicNameParameters);

        return consumer;
    }

}
