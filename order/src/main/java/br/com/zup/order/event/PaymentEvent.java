package br.com.zup.order.event;

import java.math.BigDecimal;
import java.util.Map;

public class PaymentEvent {

    private PaymentStatus paymentStatus;

    private String orderId;
    private BigDecimal amount;

    public Map<String, Integer> getItemIdQts() {
        return itemIdQts;
    }

    public void setItemIdQts(Map<String, Integer> itemIdQts) {
        this.itemIdQts = itemIdQts;
    }

    private Map<String, Integer> itemIdQts;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentEvent(String orderId, PaymentStatus paymentStatus, BigDecimal amount, Map<String, Integer> itemIdQts) {
        this.orderId = orderId;
        this.paymentStatus = paymentStatus;
        this.amount = amount;
        this.itemIdQts = itemIdQts;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
