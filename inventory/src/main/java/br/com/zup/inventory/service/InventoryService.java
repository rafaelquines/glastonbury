package br.com.zup.inventory.service;

import br.com.zup.inventory.event.OrderCreatedEvent;
import br.com.zup.inventory.event.PaymentEvent;

import java.util.HashMap;
import java.util.Map;

public interface InventoryService {
    void checkTicketAvailability(OrderCreatedEvent orderCreatedEvent);
    void rollbackTicketAvailability(PaymentEvent paymentEvent);
}
