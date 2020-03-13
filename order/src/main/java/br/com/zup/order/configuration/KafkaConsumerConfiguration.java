package br.com.zup.order.configuration;

import br.com.zup.order.event.PaymentEvent;
import br.com.zup.order.event.TicketBookedEvent;
import br.com.zup.order.service.OrderService;
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
    private OrderService orderService;

    @Autowired
    public KafkaConsumerConfiguration(@Value(value = "${spring.kafka.bootstrap-servers}") String bootstrap,
                                      ObjectMapper objectMapper, OrderService orderService) {
        this.bootstrap = bootstrap;
        this.objectMapper = objectMapper;
        this.orderService = orderService;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-group-id");
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


    @KafkaListener(topics = "booked-tickets", groupId = "order-group-id")
    public void listenBooked(String message) throws IOException {
        TicketBookedEvent event = this.objectMapper.readValue(message, TicketBookedEvent.class);
        this.orderService.updateStatus(event.getOrderId(), "booked");
        System.out.println("Order Id: " + event.getOrderId() + " Amount: " + event.getAmount() + " BOOKED");
    }

    @KafkaListener(topics = "rejected-tickets", groupId = "order-group-id")
    public void listenRejected(String message) throws IOException {
        TicketBookedEvent event = this.objectMapper.readValue(message, TicketBookedEvent.class);
        this.orderService.updateStatus(event.getOrderId(), "rejected");
        System.out.println("Order Id: " + event.getOrderId() + " REJECTED");
    }

    @KafkaListener(topics = "approved-payment", groupId = "order-group-id")
    public void listenPaymentApproved(String message) throws IOException {
        PaymentEvent event = this.objectMapper.readValue(message, PaymentEvent.class);
        this.orderService.updateStatus(event.getOrderId(), "payment-confirmed");
        System.out.println("Order Id: " + event.getOrderId() + " PAID");
    }

    @KafkaListener(topics = "rejected-payment", groupId = "order-group-id")
    public void listenPaymentRejected(String message) throws IOException {
        PaymentEvent event = this.objectMapper.readValue(message, PaymentEvent.class);
        this.orderService.updateStatus(event.getOrderId(), "payment-rejected");
        System.out.println("Order Id: " + event.getOrderId() + " PAYMENT REJECTED");
    }
}
