package br.com.zup.inventory.service.impl;

import br.com.zup.inventory.event.*;
import br.com.zup.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Service
public class InventoryServiceImpl implements InventoryService {
    Map<String, Integer> ticketsAvailability;
    KafkaTemplate<String, TicketBookedEvent> bookedTpl;

    @Autowired
    public InventoryServiceImpl(KafkaTemplate<String, TicketBookedEvent> bookedTpl) {
        this.ticketsAvailability = new HashMap<String, Integer>();
        this.ticketsAvailability.put("3852cb18-9c19-4326-ac77-a1a0264bd98c", 100);
        this.bookedTpl = bookedTpl;
    }

    @Override
    public void checkTicketAvailability(OrderCreatedEvent orderCreatedEvent) {
        System.out.println("Checking availability");
        Set<Map.Entry<String, Integer>> entries = orderCreatedEvent.getItemIdQts().entrySet();
        Iterator<Map.Entry<String, Integer>> iterator = entries.iterator();
        boolean rejected = false;
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> orderItem = iterator.next();
            int qtyAv = this.ticketsAvailability.get(orderItem.getKey());
            System.out.println("Order Item: " + orderItem.getValue() + " Qty Av: " + qtyAv + " After: " + (qtyAv - orderItem.getValue()));
            if(orderItem.getValue() > qtyAv) {
                // Exceeded
                rejected = true;
                break;
            } else {
                // OK
                // Decrement
                this.decrementTicket(orderItem.getKey(), orderItem.getValue());
                continue;
            }
            //System.out.println("Item: " + orderItem.getKey() + " => Qtd: " + orderItem.getValue());
        }
        TicketBookedEvent bookedEvent;
        System.out.println("Sending amount: " + orderCreatedEvent.getAmount());
        if(rejected) {
            bookedEvent = new TicketBookedEvent(orderCreatedEvent.getOrderId(), TicketStatus.REJECTED, TicketRejectReason.NO_AVAILABILITY, orderCreatedEvent.getAmount(), orderCreatedEvent.getItemIdQts());
            System.out.println("REJECTED");
        } else {
            bookedEvent = new TicketBookedEvent(orderCreatedEvent.getOrderId(), TicketStatus.BOOKED, TicketRejectReason.NONE, orderCreatedEvent.getAmount(), orderCreatedEvent.getItemIdQts());
            System.out.println("BOOKED");
        }
        this.bookedTpl.send(rejected ? "rejected-tickets" : "booked-tickets", bookedEvent);
    }

    @Override
    public void rollbackTicketAvailability(PaymentEvent paymentEvent) {
        System.out.println("Rollbacking availability");
        Set<Map.Entry<String, Integer>> entries = paymentEvent.getItemIdQts().entrySet();
        Iterator<Map.Entry<String, Integer>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> orderItem = iterator.next();
            int qtyAv = this.ticketsAvailability.get(orderItem.getKey());
            System.out.println("Order Item: " + orderItem.getValue() + " Qty Av: " + qtyAv + " After: " + (qtyAv + orderItem.getValue()));
            // Increment
            this.incrementTickets(orderItem.getKey(), orderItem.getValue());
        }
    }

    private void decrementTicket(String id, int qty) {
        this.ticketsAvailability.computeIfPresent(id, (k, v) -> v - qty);
    }

    private void incrementTickets(String id, int qty) {
        this.ticketsAvailability.computeIfPresent(id, (k, v) -> v + qty);
    }
}
