package com.eventify.repository;

import com.eventify.model.Stall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StallRepository extends JpaRepository<Stall, UUID> {
    Optional<Stall> findByStallNumber(int stallNumber);

    @Query("SELECT s FROM Stall s WHERE NOT EXISTS (SELECT v FROM Vendor v WHERE v.stall = s) ORDER BY s.stallNumber ASC")
    List<Stall> findFreeStalls();
}
