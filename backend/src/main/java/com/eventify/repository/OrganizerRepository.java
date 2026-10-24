package com.eventify.repository;

import com.eventify.model.Organizer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OrganizerRepository extends JpaRepository<Organizer, UUID> {
    List<Organizer> findByLastNameIgnoreCaseOrderByFirstNameAsc(String lastName);

    @Query("SELECT DISTINCT e.organizer FROM EventBooking e WHERE e.vendor.id = :vendorId")
    List<Organizer> findDistinctOrganizersByVendorId(@Param("vendorId") UUID vendorId);
}
