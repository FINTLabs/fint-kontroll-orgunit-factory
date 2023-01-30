package no.fintlabs.orgUnit;

import no.fintlabs.kafka.entity.EntityProducer;
import no.fintlabs.kafka.entity.EntityProducerFactory;
import no.fintlabs.kafka.entity.topic.EntityTopicNameParameters;
import no.fintlabs.orgUnit.OrgUnit;
import org.springframework.stereotype.Service;

@Service
public class OrgUnitEntityProducerService {
    private final EntityProducer<OrgUnit> entityProducer;
    private final EntityTopicNameParameters entityTopicNameParameters;

    public OrgUnitEntityProducerService(EntityProducerFactory entityProducerFactory) {
        entityProducer = entityProducerFactory.createProducer(OrgUnit.class);
        entityTopicNameParameters =  EntityTopicNameParameters
                .builder()
                .resource("orgunit")
                .build();
    }

    public void publish(OrgUnit orgUnit){
      //TODO: Finne ut ang resourceID
    }
}
