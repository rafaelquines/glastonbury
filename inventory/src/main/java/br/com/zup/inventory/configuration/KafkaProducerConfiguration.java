package br.com.zup.inventory.configuration;

import br.com.zup.inventory.event.TicketBookedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfiguration {

    private String bootstrap;
    private ObjectMapper objectMapper;

    @Autowired
    public KafkaProducerConfiguration(@Value(value = "${spring.kafka.bootstrap-servers}") String bootstrap,
                                      ObjectMapper objectMapper) {
        this.bootstrap = bootstrap;
        this.objectMapper = objectMapper;
    }
    @Bean
    public DefaultKafkaProducerFactory messageProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public NewTopic createBookedTicketsTopic() {
        return new NewTopic("booked-tickets", 1, (short) 1);
    }

    @Bean
    public NewTopic createRejectedTicketsTopic() {
        return new NewTopic("rejected-tickets", 1, (short) 1);
    }

    @Bean
    public KafkaTemplate<String, TicketBookedEvent> ticketBookedKafkaTemplate() {
        return new KafkaTemplate<String, TicketBookedEvent>(messageProducerFactory());
    }

}
