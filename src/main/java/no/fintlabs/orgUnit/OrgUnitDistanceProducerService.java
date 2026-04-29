package no.fintlabs.orgUnit;


import lombok.extern.slf4j.Slf4j;
import no.fintlabs.cache.FintCache;
import no.novari.kafka.producing.ParameterizedProducerRecord;
import no.novari.kafka.producing.ParameterizedTemplate;
import no.novari.kafka.producing.ParameterizedTemplateFactory;
import no.novari.kafka.topic.EntityTopicService;
import no.novari.kafka.topic.configuration.EntityCleanupFrequency;
import no.novari.kafka.topic.configuration.EntityTopicConfiguration;
import no.novari.kafka.topic.name.EntityTopicNameParameters;
import no.novari.kafka.topic.name.TopicNamePrefixParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@Slf4j
public class OrgUnitDistanceProducerService {
    private final ParameterizedTemplate<OrgUnitDistance> parameterizedTemplate;
    private final EntityTopicNameParameters entityTopicNameParameters;
    private final FintCache<String,OrgUnitDistance> orgUnitDistanceCache;

    public OrgUnitDistanceProducerService(ParameterizedTemplateFactory parameterizedTemplateFactory,
                                          FintCache<String,OrgUnitDistance> orgUnitDistanceCache,
                                          EntityTopicService entityTopicService){
        this.parameterizedTemplate = parameterizedTemplateFactory.createTemplate(OrgUnitDistance.class);
        this.orgUnitDistanceCache = orgUnitDistanceCache;

        entityTopicNameParameters = EntityTopicNameParameters
               .builder()
               .topicNamePrefixParameters(TopicNamePrefixParameters
                       .stepBuilder()
                       .orgIdApplicationDefault()
                       .domainContextApplicationDefault().build()
               ).resourceName("orgunitdistance")
               .build();

        entityTopicService.createOrModifyTopic(entityTopicNameParameters, EntityTopicConfiguration.stepBuilder()
                .partitions(1)
                .lastValueRetainedForever()
                .nullValueRetentionTime(Duration.ZERO)
                .cleanupFrequency(EntityCleanupFrequency.NORMAL)
                .build());
    }

    public List<OrgUnitDistance> publish(List<OrgUnitDistance> orgUnitDistances) {
        return orgUnitDistances
                .stream()
                .filter(orgUnitDistance -> orgUnitDistanceCache
                        .getOptional(orgUnitDistance.getId())
                        .map(publishedOrgUnitDistance -> !orgUnitDistance.equals(publishedOrgUnitDistance))
                        .orElse(true)
                )
                .peek(this::publish)
                .toList();
    }

    private void publish(OrgUnitDistance orgUnitDistance) {
        String key = orgUnitDistance.getId();
        parameterizedTemplate.send(
                ParameterizedProducerRecord.<OrgUnitDistance>builder()
                        .topicNameParameters(entityTopicNameParameters)
                        .key(key)
                        .value(orgUnitDistance)
                        .build()
        );
    }

}
