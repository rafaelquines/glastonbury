package br.com.zup.payment.service.impl;

import br.com.zup.payment.event.PaymentEvent;
import br.com.zup.payment.event.PaymentStatus;
import br.com.zup.payment.event.TicketBookedEvent;
import br.com.zup.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentServiceImpl implements PaymentService {

    private KafkaTemplate<String, PaymentEvent> paymentTpl;

    @Autowired
    public PaymentServiceImpl(KafkaTemplate<String, PaymentEvent> paymentTpl) {
        this.paymentTpl = paymentTpl;
    }


    @Override
    public void process(TicketBookedEvent bookedEvent) {
        PaymentEvent paymentEvent;
        boolean rejected = false;
        if(bookedEvent.getAmount().compareTo(new BigDecimal(100)) > 0) {
            //REJECTED
            rejected = true;
            paymentEvent = new PaymentEvent(bookedEvent.getOrderId(), PaymentStatus.PAYMENT_REJECTED, bookedEvent.getAmount(), bookedEvent.getItemIdQts());
            System.out.println("Payment Rejected. Amount: " + bookedEvent.getAmount());
        } else {
            //ACCEPTED
            paymentEvent = new PaymentEvent(bookedEvent.getOrderId(), PaymentStatus.PAYMENT_APPROVED, bookedEvent.getAmount(), bookedEvent.getItemIdQts());
            System.out.println("Payment Approved. Amount: " + bookedEvent.getAmount());
        }
        this.paymentTpl.send(rejected ? "rejected-payment" : "approved-payment", paymentEvent);
    }
}
