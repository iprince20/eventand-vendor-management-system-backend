package com.eventify.repository;

import com.eventify.model.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ServiceTypeRepository extends JpaRepository<ServiceType, UUID> {
    @Query("SELECT s FROM ServiceType s WHERE s.vendors IS EMPTY")
    List<ServiceType> findUnusedServiceTypes();
}
