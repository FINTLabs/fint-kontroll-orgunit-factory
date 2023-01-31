package no.fintlabs.orgUnit;

import no.fintlabs.kafka.entity.EntityProducer;
import no.fintlabs.kafka.entity.EntityProducerFactory;
import no.fintlabs.kafka.entity.EntityProducerRecord;
import no.fintlabs.kafka.entity.topic.EntityTopicNameParameters;
import no.fintlabs.kafka.entity.topic.EntityTopicService;
import org.springframework.stereotype.Service;

@Service
public class OrgUnitEntityProducerService {
    private final EntityProducer<OrgUnit> entityProducer;
    private final EntityTopicNameParameters entityTopicNameParameters;

    public OrgUnitEntityProducerService(
            EntityProducerFactory entityProducerFactory,
            EntityTopicService entityTopicService
    ) {
        entityProducer = entityProducerFactory.createProducer(OrgUnit.class);
        entityTopicNameParameters =  EntityTopicNameParameters
                .builder()
                .resource("orgunit")
                .build();
        entityTopicService.ensureTopic(entityTopicNameParameters,0);
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
