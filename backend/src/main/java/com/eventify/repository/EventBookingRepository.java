package com.eventify.repository;

import com.eventify.model.EventBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventBookingRepository extends JpaRepository<EventBooking, UUID> {
}
