package br.com.zup.payment.service;

import br.com.zup.payment.event.TicketBookedEvent;

public interface PaymentService {
    void process(TicketBookedEvent bookedEvent);
}
