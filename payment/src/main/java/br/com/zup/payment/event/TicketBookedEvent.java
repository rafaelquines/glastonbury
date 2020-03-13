package br.com.zup.payment.event;

import java.math.BigDecimal;
import java.util.Map;

public class TicketBookedEvent {

    private String orderId;
    private TicketStatus status;
    private BigDecimal amount;

    public Map<String, Integer> getItemIdQts() {
        return itemIdQts;
    }

    public void setItemIdQts(Map<String, Integer> itemIdQts) {
        this.itemIdQts = itemIdQts;
    }

    private Map<String, Integer> itemIdQts;

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public TicketRejectReason getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(TicketRejectReason rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    private TicketRejectReason rejectionReason;

    public TicketBookedEvent(String orderId, TicketStatus status, TicketRejectReason rejectionReason, BigDecimal amount, Map<String, Integer> itemIdQts) {
        this.orderId = orderId;
        this.status = status;
        this.rejectionReason = rejectionReason;
        this.amount = amount;
        this.itemIdQts = itemIdQts;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
