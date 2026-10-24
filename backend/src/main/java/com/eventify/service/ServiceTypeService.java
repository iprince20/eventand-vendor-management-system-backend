package com.eventify.service;

import com.eventify.model.ServiceType;
import com.eventify.repository.ServiceTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ServiceTypeService {
    private final ServiceTypeRepository serviceTypes;

    public ServiceTypeService(ServiceTypeRepository serviceTypes) {
        this.serviceTypes = serviceTypes;
    }

    public List<ServiceType> getAll() {
        return serviceTypes.findAll();
    }

    public Optional<ServiceType> getById(UUID id) {
        return serviceTypes.findById(id);
    }

    public String save(ServiceType serviceType) {
        serviceTypes.save(serviceType);
        return "Service type saved successfully";
    }

    public String update(UUID id, ServiceType input) {
        return serviceTypes.findById(id).map(existing -> {
            existing.setName(input.getName());
            serviceTypes.save(existing);
            return "Service type updated successfully";
        }).orElse("The service type with that id does not exist");
    }

    public String delete(UUID id) {
        return serviceTypes.findById(id).map(existing -> {
            serviceTypes.delete(existing);
            return "Service type deleted successfully";
        }).orElse("The service type with that id does not exist");
    }
}
