package com.eventify.service;

import com.eventify.model.BookingStatus;
import com.eventify.repository.OrganizerRepository;
import com.eventify.repository.VendorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class DashboardService {
    private final OrganizerRepository organizers;
    private final VendorRepository vendors;
    private final StallService stalls;
    private final EventBookingService eventBookings;
    private final VendorInvoiceService invoices;

    public DashboardService(OrganizerRepository organizers, VendorRepository vendors, StallService stalls,
                            EventBookingService eventBookings, VendorInvoiceService invoices) {
        this.organizers = organizers;
        this.vendors = vendors;
        this.stalls = stalls;
        this.eventBookings = eventBookings;
        this.invoices = invoices;
    }

    public Map<String, Long> summary() {
        Map<String, Long> summary = new LinkedHashMap<>();
        summary.put("organizers", organizers.count());
        summary.put("vendors", vendors.count());
        summary.put("freeStalls", (long) stalls.findFree().size());
        summary.put("bookingsToday", eventBookings.countByDate(LocalDate.now()));
        summary.put("requested", eventBookings.countByStatus(BookingStatus.REQUESTED));
        summary.put("invoices", invoices.count());
        return summary;
    }
}
