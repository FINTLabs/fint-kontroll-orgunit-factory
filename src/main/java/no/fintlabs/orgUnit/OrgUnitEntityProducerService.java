package no.fintlabs.orgUnit;

import no.fintlabs.cache.FintCache;
import no.fintlabs.kafka.entity.EntityProducer;
import no.fintlabs.kafka.entity.EntityProducerFactory;
import no.fintlabs.kafka.entity.EntityProducerRecord;
import no.fintlabs.kafka.entity.topic.EntityTopicNameParameters;
import no.fintlabs.kafka.entity.topic.EntityTopicService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrgUnitEntityProducerService {
    private final EntityProducer<OrgUnit> entityProducer;
    private final EntityTopicNameParameters entityTopicNameParameters;
    private final FintCache<String,Integer> publishedHashCache;

    public OrgUnitEntityProducerService(
            EntityProducerFactory entityProducerFactory,
            EntityTopicService entityTopicService,
            FintCache<String, Integer> publishedHashCache) {
        entityProducer = entityProducerFactory.createProducer(OrgUnit.class);
        this.publishedHashCache = publishedHashCache;
        entityTopicNameParameters =  EntityTopicNameParameters
                .builder()
                .resource("orgunit")
                .build();
        entityTopicService.ensureTopic(entityTopicNameParameters,0);
    }


    public List<OrgUnit> publish(List<OrgUnit> orgUnits){
        return orgUnits
                .stream()
                .filter(orgUnit -> publishedHashCache
                        .getOptional(orgUnit.getResourceId())
                        .map(publishedHashCache -> publishedHashCache!= orgUnit.hashCode())
                        .orElse(true)
                )
                .peek(this::publish)
                .toList();
    }

    public void publish(OrgUnit orgUnit){
      String key = orgUnit.getResourceId();
      entityProducer.send(
              EntityProducerRecord.<OrgUnit>builder()
                      .topicNameParameters(entityTopicNameParameters)
                      .key(key)
                      .value(orgUnit)
                      .build()
      );
    }
}
