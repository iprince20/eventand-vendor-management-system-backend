package com.eventify.repository;

import com.eventify.model.Stall;
import com.eventify.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface VendorRepository extends JpaRepository<Vendor, UUID> {
    @Query("SELECT DISTINCT v FROM Vendor v JOIN v.serviceTypes s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Vendor> findByServiceTypeName(@Param("name") String name);

    boolean existsByStall(Stall stall);
}
