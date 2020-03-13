package br.com.zup.payment.configuration;

import br.com.zup.payment.event.TicketBookedEvent;
import br.com.zup.payment.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfiguration {

    private String bootstrap;
    private ObjectMapper objectMapper;
    private PaymentService paymentService;

    @Autowired
    public KafkaConsumerConfiguration(@Value(value = "${spring.kafka.bootstrap-servers}") String bootstrap,
                                      ObjectMapper objectMapper, PaymentService paymentService) {
        this.bootstrap = bootstrap;
        this.objectMapper = objectMapper;
        this.paymentService = paymentService;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "payment-group-id");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String>
    kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }


    @KafkaListener(topics = "booked-tickets", groupId = "payment-group-id")
    public void listenBooked(String message) throws IOException {
        TicketBookedEvent event = this.objectMapper.readValue(message, TicketBookedEvent.class);
        System.out.println("Trying pay OrderId: " + event.getOrderId() + " Amount: " + event.getAmount());
        this.paymentService.process(event);
    }

}
