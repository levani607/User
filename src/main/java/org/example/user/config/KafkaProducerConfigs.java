package org.example.user.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.user.model.request.SomeTestClass;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnExpression("${app.cfg.kafka.enabled:true}")
public class KafkaProducerConfigs {

    @Value("${myapp.bootstrap-servers}")
    private String bootstrapAddress;

    private Map<String, Object> getConfigProps() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return configProps;
    }

    @Bean
    public ProducerFactory<String, SomeTestClass> createDocumentProducerFactory() {
        return new DefaultKafkaProducerFactory<>(getConfigProps());
    }
    @Bean
    public KafkaTemplate<String, SomeTestClass> createDocumentRequestTemplate(ProducerFactory<String, SomeTestClass> createDocumentProducerFactory) {
        return new KafkaTemplate<>(createDocumentProducerFactory);
    }
}
