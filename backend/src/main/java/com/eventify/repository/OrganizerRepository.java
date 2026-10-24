package com.eventify.repository;

import com.eventify.model.Organizer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrganizerRepository extends JpaRepository<Organizer, UUID> {
    List<Organizer> findByLastNameIgnoreCaseOrderByFirstNameAsc(String lastName);
}
