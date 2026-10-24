package com.eventify.service;

import com.eventify.model.BookingStatus;
import com.eventify.model.EventBooking;
import com.eventify.model.Organizer;
import com.eventify.model.Vendor;
import com.eventify.repository.EventBookingRepository;
import com.eventify.repository.OrganizerRepository;
import com.eventify.repository.VendorInvoiceRepository;
import com.eventify.repository.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventBookingServiceTest {
    @Mock
    private EventBookingRepository eventBookings;

    @Mock
    private OrganizerRepository organizers;

    @Mock
    private VendorRepository vendors;

    @Mock
    private VendorInvoiceRepository invoices;

    private EventBookingService service;

    @BeforeEach
    void setUp() {
        service = new EventBookingService(eventBookings, organizers, vendors, invoices);
    }

    @Test
    void rejectsEventDateBeforeToday() {
        EventBooking booking = booking(LocalDate.now().minusDays(1));

        String result = service.save(booking);

        assertEquals("Event booking date cannot be in the past", result);
        verify(eventBookings, never()).save(any(EventBooking.class));
    }

    @Test
    void savesNewBookingAsRequested() {
        UUID vendorId = UUID.randomUUID();
        UUID organizerId = UUID.randomUUID();
        Vendor vendor = org.mockito.Mockito.mock(Vendor.class);
        Organizer organizer = org.mockito.Mockito.mock(Organizer.class);
        EventBooking booking = booking(LocalDate.now().plusDays(1));
        when(booking.getVendor()).thenReturn(vendor);
        when(booking.getOrganizer()).thenReturn(organizer);
        when(vendor.getId()).thenReturn(vendorId);
        when(organizer.getId()).thenReturn(organizerId);
        when(vendors.findById(vendorId)).thenReturn(Optional.of(vendor));
        when(organizers.findById(organizerId)).thenReturn(Optional.of(organizer));
        when(eventBookings.existsByVendorAndEventDateAndStatusNot(
                vendor, booking.getEventDate(), BookingStatus.DECLINED)).thenReturn(false);

        String result = service.save(booking);

        assertEquals("Event booking saved successfully", result);
        verify(booking).setStatus(BookingStatus.REQUESTED);
        verify(eventBookings).save(booking);
    }

    @Test
    void rejectsBookingWhenVendorHasNonDeclinedBookingOnDate() {
        UUID vendorId = UUID.randomUUID();
        UUID organizerId = UUID.randomUUID();
        Vendor vendor = org.mockito.Mockito.mock(Vendor.class);
        Organizer organizer = org.mockito.Mockito.mock(Organizer.class);
        EventBooking booking = booking(LocalDate.now().plusDays(1));
        when(booking.getVendor()).thenReturn(vendor);
        when(booking.getOrganizer()).thenReturn(organizer);
        when(vendor.getId()).thenReturn(vendorId);
        when(organizer.getId()).thenReturn(organizerId);
        when(vendors.findById(vendorId)).thenReturn(Optional.of(vendor));
        when(organizers.findById(organizerId)).thenReturn(Optional.of(organizer));
        when(eventBookings.existsByVendorAndEventDateAndStatusNot(
                vendor, booking.getEventDate(), BookingStatus.DECLINED)).thenReturn(true);

        String result = service.save(booking);

        assertEquals("Vendor is already booked for an event on that date", result);
        verify(eventBookings, never()).save(any(EventBooking.class));
    }

    @Test
    void permitsOnlyRequestedToAcceptedOrDeclined() {
        UUID id = UUID.randomUUID();
        EventBooking booking = org.mockito.Mockito.mock(EventBooking.class);
        when(eventBookings.findById(id)).thenReturn(Optional.of(booking));
        when(booking.getStatus()).thenReturn(BookingStatus.DELIVERED);

        String result = service.updateStatus(id, BookingStatus.REQUESTED);

        assertEquals("Cannot change status from DELIVERED to REQUESTED", result);
        verify(eventBookings, never()).save(any(EventBooking.class));
    }

    @Test
    void acceptsRequestedToAccepted() {
        UUID id = UUID.randomUUID();
        EventBooking booking = org.mockito.Mockito.mock(EventBooking.class);
        when(eventBookings.findById(id)).thenReturn(Optional.of(booking));
        when(booking.getStatus()).thenReturn(BookingStatus.REQUESTED);
        when(eventBookings.save(booking)).thenReturn(booking);

        String result = service.updateStatus(id, BookingStatus.ACCEPTED);

        assertEquals("Event booking status updated successfully", result);
        verify(booking).setStatus(BookingStatus.ACCEPTED);
        verify(eventBookings).save(booking);
    }

    @Test
    void rejectsBookingDeleteWhenInvoicesExist() {
        UUID id = UUID.randomUUID();
        EventBooking booking = org.mockito.Mockito.mock(EventBooking.class);
        when(eventBookings.findById(id)).thenReturn(Optional.of(booking));
        when(invoices.existsByEventBooking(booking)).thenReturn(true);

        String result = service.delete(id);

        assertEquals("That event booking has vendor invoices, delete them first", result);
        verify(eventBookings, never()).delete(booking);
    }

    private EventBooking booking(LocalDate eventDate) {
        EventBooking booking = org.mockito.Mockito.mock(EventBooking.class);
        when(booking.getEventDate()).thenReturn(eventDate);
        return booking;
    }
}
