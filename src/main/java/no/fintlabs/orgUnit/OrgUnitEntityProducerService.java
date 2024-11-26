package no.fintlabs.orgUnit;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.cache.FintCache;
import no.fintlabs.kafka.entity.EntityProducer;
import no.fintlabs.kafka.entity.EntityProducerFactory;
import no.fintlabs.kafka.entity.EntityProducerRecord;
import no.fintlabs.kafka.entity.topic.EntityTopicNameParameters;
import no.fintlabs.kafka.entity.topic.EntityTopicService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class OrgUnitEntityProducerService {
    private final EntityProducer<OrgUnit> entityProducer;
    private final EntityTopicNameParameters entityTopicNameParameters;
    private final FintCache<String,OrgUnit> publishedOrgUnitCache;

    public OrgUnitEntityProducerService(
            EntityProducerFactory entityProducerFactory,
            EntityTopicService entityTopicService,
            FintCache<String, OrgUnit> publishedOrgUnitCache) {
        entityProducer = entityProducerFactory.createProducer(OrgUnit.class);
        this.publishedOrgUnitCache = publishedOrgUnitCache;

        entityTopicNameParameters =  EntityTopicNameParameters
                .builder()
                .resource("orgunit")
                .build();
        entityTopicService.ensureTopic(entityTopicNameParameters,0);
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
      entityProducer.send(
              EntityProducerRecord.<OrgUnit>builder()
                      .topicNameParameters(entityTopicNameParameters)
                      .key(key)
                      .value(orgUnit)
                      .build()
      );
    }
}
