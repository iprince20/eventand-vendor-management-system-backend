package com.eventify.repository;

import com.eventify.model.EventBooking;
import com.eventify.model.VendorInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface VendorInvoiceRepository extends JpaRepository<VendorInvoice, UUID> {
    @Query("SELECT i FROM VendorInvoice i WHERE i.eventBooking.organizer.organizerCode = :organizerCode ORDER BY i.issuedDate DESC")
    List<VendorInvoice> findByOrganizerCodeOrderByIssuedDateDesc(@Param("organizerCode") String organizerCode);

    boolean existsByEventBooking(EventBooking eventBooking);
}
