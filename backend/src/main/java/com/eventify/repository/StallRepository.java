package com.eventify.repository;

import com.eventify.model.Stall;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StallRepository extends JpaRepository<Stall, UUID> {
}
