package com.eventify.service;

import com.eventify.model.EventBooking;
import com.eventify.model.BookingStatus;
import com.eventify.model.Organizer;
import com.eventify.model.Vendor;
import com.eventify.repository.EventBookingRepository;
import com.eventify.repository.OrganizerRepository;
import com.eventify.repository.VendorInvoiceRepository;
import com.eventify.repository.VendorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventBookingService {
    private final EventBookingRepository eventBookings;
    private final OrganizerRepository organizers;
    private final VendorRepository vendors;
    private final VendorInvoiceRepository invoices;

    public EventBookingService(EventBookingRepository eventBookings, OrganizerRepository organizers,
                               VendorRepository vendors, VendorInvoiceRepository invoices) {
        this.eventBookings = eventBookings;
        this.organizers = organizers;
        this.vendors = vendors;
        this.invoices = invoices;
    }

    public List<EventBooking> getAll() {
        return eventBookings.findAll();
    }

    public Optional<EventBooking> getById(UUID id) {
        return eventBookings.findById(id);
    }

    public String save(EventBooking booking) {
        if (booking.getEventDate() != null && booking.getEventDate().isBefore(LocalDate.now())) {
            return "Event booking date cannot be in the past";
        }
        if (booking.getVendor() == null || booking.getVendor().getId() == null) {
            return "The vendor with that id does not exist";
        }
        Optional<Vendor> vendorResult = vendors.findById(booking.getVendor().getId());
        if (vendorResult.isEmpty()) {
            return "The vendor with that id does not exist";
        }
        if (booking.getOrganizer() == null || booking.getOrganizer().getId() == null) {
            return "The organizer with that id does not exist";
        }
        Optional<Organizer> organizerResult = organizers.findById(booking.getOrganizer().getId());
        if (organizerResult.isEmpty()) {
            return "The organizer with that id does not exist";
        }
        Vendor vendor = vendorResult.get();
        if (booking.getEventDate() != null
                && eventBookings.existsByVendorAndEventDateAndStatusNot(vendor, booking.getEventDate(), BookingStatus.DECLINED)) {
            return "Vendor is already booked for an event on that date";
        }
        booking.setVendor(vendor);
        booking.setOrganizer(organizerResult.get());
        booking.setStatus(BookingStatus.REQUESTED);
        eventBookings.save(booking);
        return "Event booking saved successfully";
    }

    public String update(UUID id, EventBooking input) {
        Optional<EventBooking> existingResult = eventBookings.findById(id);
        if (existingResult.isEmpty()) {
            return "The event booking with that id does not exist";
        }

        EventBooking existing = existingResult.get();
        existing.setEventDate(input.getEventDate());
        existing.setEventName(input.getEventName());
        existing.setGuests(input.getGuests());
        existing.setOrganizer(resolveOrganizer(input.getOrganizer()));
        existing.setVendor(resolveVendor(input.getVendor()));
        eventBookings.save(existing);
        return "Event booking updated successfully";
    }

    public String delete(UUID id) {
        Optional<EventBooking> bookingResult = eventBookings.findById(id);
        if (bookingResult.isEmpty()) {
            return "The event booking with that id does not exist";
        }
        if (invoices.existsByEventBooking(bookingResult.get())) {
            return "That event booking has vendor invoices, delete them first";
        }
        eventBookings.delete(bookingResult.get());
        return "Event booking deleted successfully";
    }

    public List<EventBooking> findByStatus(BookingStatus status) {
        return eventBookings.findByStatusOrderByEventDateAsc(status);
    }

    public List<EventBooking> findBetween(java.time.LocalDate start, java.time.LocalDate end) {
        return eventBookings.findByEventDateBetweenOrderByEventDateAsc(start, end);
    }

    public String updateStatus(UUID id, BookingStatus newStatus) {
        Optional<EventBooking> bookingResult = eventBookings.findById(id);
        if (bookingResult.isEmpty()) {
            return "The event booking with that id does not exist";
        }

        EventBooking booking = bookingResult.get();
        BookingStatus currentStatus = booking.getStatus();
        boolean allowed = currentStatus == BookingStatus.REQUESTED
                && (newStatus == BookingStatus.ACCEPTED || newStatus == BookingStatus.DECLINED)
                || currentStatus == BookingStatus.ACCEPTED
                && (newStatus == BookingStatus.DELIVERED || newStatus == BookingStatus.DECLINED);
        if (!allowed) {
            return "Cannot change status from " + currentStatus + " to " + newStatus;
        }
        booking.setStatus(newStatus);
        eventBookings.save(booking);
        return "Event booking status updated successfully";
    }

    public List<Object[]> statsByStatus() {
        return eventBookings.countBookingsByStatus();
    }

    @Transactional
    public String cancelDay(UUID vendorId, LocalDate date) {
        int cancelled = eventBookings.declineBookingsForVendorAndDate(
                vendorId, date, BookingStatus.DECLINED, BookingStatus.DELIVERED);
        return cancelled + " event bookings cancelled";
    }

    private Organizer resolveOrganizer(Organizer input) {
        if (input == null || input.getId() == null) {
            return null;
        }
        return organizers.findById(input.getId()).orElse(null);
    }

    private Vendor resolveVendor(Vendor input) {
        if (input == null || input.getId() == null) {
            return null;
        }
        return vendors.findById(input.getId()).orElse(null);
    }
}
