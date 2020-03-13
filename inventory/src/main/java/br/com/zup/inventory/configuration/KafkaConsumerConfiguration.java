package br.com.zup.inventory.configuration;

import br.com.zup.inventory.event.OrderCreatedEvent;
import br.com.zup.inventory.event.PaymentEvent;
import br.com.zup.inventory.service.InventoryService;
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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Configuration
public class KafkaConsumerConfiguration {

    private String bootstrap;
    private ObjectMapper objectMapper;
    private InventoryService inventotyService;

    @Autowired
    public KafkaConsumerConfiguration(@Value(value = "${spring.kafka.bootstrap-servers}") String bootstrap,
                                      ObjectMapper objectMapper, InventoryService inventoryService) {
        this.bootstrap = bootstrap;
        this.objectMapper = objectMapper;
        this.inventotyService = inventoryService;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "inventory-group-id");
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


    @KafkaListener(topics = "created-orders", groupId = "inventory-group-id")
    public void listenCreatedOrders(String message) throws IOException {
        OrderCreatedEvent event = this.objectMapper.readValue(message, OrderCreatedEvent.class);
        System.out.println("Received created-orders: " + event.getCustomerId() + " Amount: " + event.getAmount());
        Map<String, Integer> items = event.getItemIdQts();
        Set<Map.Entry<String, Integer>> entries = items.entrySet();
        Iterator<Map.Entry<String, Integer>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> item = iterator.next();
            System.out.println("Item: " + item.getKey() + " => Qtd: " + item.getValue());
        }
        this.inventotyService.checkTicketAvailability(event);
    }

    @KafkaListener(topics = "rejected-payment", groupId = "inventory-group-id")
    public void listenRejectedPayment(String message) throws IOException {
        PaymentEvent event = this.objectMapper.readValue(message, PaymentEvent.class);
        System.out.println("Rejected payment order: " + event.getOrderId());
        Map<String, Integer> items = event.getItemIdQts();
        Set<Map.Entry<String, Integer>> entries = items.entrySet();
        Iterator<Map.Entry<String, Integer>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> item = iterator.next();
            System.out.println("Item: " + item.getKey() + " => Qtd: " + item.getValue());
        }
        this.inventotyService.rollbackTicketAvailability(event);
    }
}
