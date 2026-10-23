package com.eventify.repository;

import com.eventify.model.BookingStatus;
import com.eventify.model.EventBooking;
import com.eventify.model.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface EventBookingRepository extends JpaRepository<EventBooking, UUID> {
    List<EventBooking> findByStatusOrderByEventDateAsc(BookingStatus status);

    List<EventBooking> findByEventDateBetweenOrderByEventDateAsc(LocalDate start, LocalDate end);

    boolean existsByVendorAndEventDateAndStatusNot(Vendor vendor, LocalDate eventDate, BookingStatus status);

    @Query("SELECT e.status, COUNT(e) FROM EventBooking e GROUP BY e.status")
    List<Object[]> countBookingsByStatus();

    long countByStatus(BookingStatus status);

    long countByEventDate(LocalDate eventDate);

    @Modifying
    @Query("UPDATE EventBooking e SET e.status = :declined WHERE e.vendor.id = :vendorId AND e.eventDate = :eventDate AND e.status <> :delivered")
    int declineBookingsForVendorAndDate(
            @Param("vendorId") UUID vendorId,
            @Param("eventDate") LocalDate eventDate,
            @Param("declined") BookingStatus declined,
            @Param("delivered") BookingStatus delivered
    );
}
