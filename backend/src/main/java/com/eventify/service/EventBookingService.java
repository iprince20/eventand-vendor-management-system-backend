package com.eventify.service;

import com.eventify.model.EventBooking;
import com.eventify.model.Organizer;
import com.eventify.model.Vendor;
import com.eventify.repository.EventBookingRepository;
import com.eventify.repository.OrganizerRepository;
import com.eventify.repository.VendorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventBookingService {
    private final EventBookingRepository eventBookings;
    private final OrganizerRepository organizers;
    private final VendorRepository vendors;

    public EventBookingService(EventBookingRepository eventBookings, OrganizerRepository organizers,
                               VendorRepository vendors) {
        this.eventBookings = eventBookings;
        this.organizers = organizers;
        this.vendors = vendors;
    }

    public List<EventBooking> getAll() {
        return eventBookings.findAll();
    }

    public Optional<EventBooking> getById(UUID id) {
        return eventBookings.findById(id);
    }

    public String save(EventBooking booking) {
        booking.setOrganizer(resolveOrganizer(booking.getOrganizer()));
        booking.setVendor(resolveVendor(booking.getVendor()));
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
        existing.setStatus(input.getStatus());
        existing.setOrganizer(resolveOrganizer(input.getOrganizer()));
        existing.setVendor(resolveVendor(input.getVendor()));
        eventBookings.save(existing);
        return "Event booking updated successfully";
    }

    public String delete(UUID id) {
        return eventBookings.findById(id).map(existing -> {
            eventBookings.delete(existing);
            return "Event booking deleted successfully";
        }).orElse("The event booking with that id does not exist");
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
