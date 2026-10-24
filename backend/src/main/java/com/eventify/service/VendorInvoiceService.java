package com.eventify.service;

import com.eventify.model.BookingStatus;
import com.eventify.model.EventBooking;
import com.eventify.model.VendorInvoice;
import com.eventify.repository.EventBookingRepository;
import com.eventify.repository.VendorInvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VendorInvoiceService {
    private final VendorInvoiceRepository invoices;
    private final EventBookingRepository eventBookings;

    public VendorInvoiceService(VendorInvoiceRepository invoices, EventBookingRepository eventBookings) {
        this.invoices = invoices;
        this.eventBookings = eventBookings;
    }

    @Transactional
    public String save(UUID eventBookingId, VendorInvoice invoice) {
        Optional<EventBooking> bookingResult = eventBookings.findById(eventBookingId);
        if (bookingResult.isEmpty()) {
            return "The event booking with that id does not exist";
        }
        EventBooking booking = bookingResult.get();
        if (booking.getStatus() != BookingStatus.DELIVERED) {
            return "Vendor invoices are only for DELIVERED event bookings";
        }
        invoice.setIssuedDate(LocalDate.now());
        invoice.setEventBooking(booking);
        invoices.save(invoice);
        return "Vendor invoice saved successfully";
    }

    public List<VendorInvoice> findByOrganizerCode(String organizerCode) {
        return invoices.findByOrganizerCodeOrderByIssuedDateDesc(organizerCode);
    }

    public String delete(UUID id) {
        return invoices.findById(id).map(invoice -> {
            invoices.delete(invoice);
            return "Vendor invoice deleted successfully";
        }).orElse("The vendor invoice with that id does not exist");
    }

    public long count() {
        return invoices.count();
    }
}
