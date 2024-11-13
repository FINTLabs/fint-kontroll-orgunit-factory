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
public class OrgUnitDistanceProducerService {
    private final EntityProducer<OrgUnitDistance> entityProducer;
    private final EntityTopicNameParameters entityTopicNameParameters;
    private final FintCache<String,OrgUnitDistance> orgUnitDistanceCache;

    public OrgUnitDistanceProducerService(
            EntityProducerFactory entityProducerFactory,
            EntityTopicService entityTopicService, FintCache<String, OrgUnitDistance> orgUnitDistanceCache
    ){
        entityProducer = entityProducerFactory.createProducer(OrgUnitDistance.class);
        this.orgUnitDistanceCache = orgUnitDistanceCache;
        entityTopicNameParameters = EntityTopicNameParameters
                .builder()
                .resource("orgunitdistance")
                .build();
        entityTopicService.ensureTopic(entityTopicNameParameters,0);
    }

    public List<OrgUnitDistance> publish(List<OrgUnitDistance> orgUnitDistances) {
        return orgUnitDistances
                .stream()
                .filter(orgUnitDistance -> orgUnitDistanceCache
                        .getOptional(orgUnitDistance.getKey())
                        .map(publishedOrgUnitDistance -> !orgUnitDistance.equals(publishedOrgUnitDistance))
                        .orElse(true)
                )
                .peek(this::publish)
                .toList();
    }

    private void publish(OrgUnitDistance orgUnitDistance) {
        String key = orgUnitDistance.getKey();
        entityProducer.send(
                EntityProducerRecord.<OrgUnitDistance>builder()
                        .topicNameParameters(entityTopicNameParameters)
                        .key(key)
                        .value(orgUnitDistance)
                        .build()
        );
    }

}
