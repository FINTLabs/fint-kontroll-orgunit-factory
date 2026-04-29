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
public class OrgUnitEntityProducerService {

    private final ParameterizedTemplate<OrgUnit> parameterizedTemplate;
    private final EntityTopicNameParameters entityTopicNameParameters;
    private final FintCache<String,OrgUnit> publishedOrgUnitCache;

    public OrgUnitEntityProducerService(
            ParameterizedTemplateFactory parameterizedTemplateFactory,
            EntityTopicService entityTopicService,
            FintCache<String, OrgUnit> publishedOrgUnitCache
    ) {
        this.parameterizedTemplate = parameterizedTemplateFactory.createTemplate(OrgUnit.class);
        this.publishedOrgUnitCache = publishedOrgUnitCache;

        entityTopicNameParameters =  EntityTopicNameParameters
                .builder()
                .topicNamePrefixParameters(TopicNamePrefixParameters
                        .stepBuilder()
                        .orgIdApplicationDefault()
                        .domainContextApplicationDefault().build())
                .resourceName("orgunit")
                .build();

        entityTopicService.createOrModifyTopic(entityTopicNameParameters, EntityTopicConfiguration.stepBuilder()
                .partitions(1)
                .lastValueRetainedForever()
                .nullValueRetentionTime(Duration.ofDays(7))
                .cleanupFrequency(EntityCleanupFrequency.NORMAL)
                .build()
        );
    }


    public List<OrgUnit> publish(List<OrgUnit> orgUnits){
        return orgUnits
                .stream()
                .filter(orgUnit -> publishedOrgUnitCache
                        .getOptional(orgUnit.getResourceId())
                        .map(publishedOrgUnit -> !orgUnit.equals(publishedOrgUnit))
                        .orElse(true)
                )
                .peek(this::publish)
                .toList();
    }


    public void publish(OrgUnit orgUnit){
      String key = orgUnit.getResourceId();
        log.info("Publishing to kafka: {}", orgUnit.getResourceId());
      parameterizedTemplate.send(
              ParameterizedProducerRecord.<OrgUnit>builder()
                      .topicNameParameters(entityTopicNameParameters)
                      .key(key)
                      .value(orgUnit)
                      .build()
      );
    }
}
