package com.eventify.repository;

import com.eventify.model.BookingStatus;
import com.eventify.model.EventBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface EventBookingRepository extends JpaRepository<EventBooking, UUID> {
    List<EventBooking> findByStatusOrderByEventDateAsc(BookingStatus status);

    List<EventBooking> findByEventDateBetweenOrderByEventDateAsc(LocalDate start, LocalDate end);
}
