package com.eventify.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "vendor_invoice")
public class VendorInvoice {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String serviceName;
    private String notes;
    private int amount;
    private LocalDate issuedDate;

    @ManyToOne
    @JoinColumn(name = "event_booking_id")
    private EventBooking eventBooking;

    protected VendorInvoice() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public LocalDate getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(LocalDate issuedDate) {
        this.issuedDate = issuedDate;
    }

    public EventBooking getEventBooking() {
        return eventBooking;
    }

    public void setEventBooking(EventBooking eventBooking) {
        this.eventBooking = eventBooking;
    }
}
