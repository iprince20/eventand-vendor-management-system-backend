package com.eventify.repository;

import com.eventify.model.BookingStatus;
import com.eventify.model.EventBooking;
import com.eventify.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface EventBookingRepository extends JpaRepository<EventBooking, UUID> {
    boolean existsByVendorAndEventDateAndStatusNot(
            Vendor vendor, LocalDate eventDate, BookingStatus status);

    List<EventBooking> findByStatusOrderByEventDateAsc(BookingStatus status);

    List<EventBooking> findByEventDateBetweenOrderByEventDateAsc(LocalDate start, LocalDate end);
}
