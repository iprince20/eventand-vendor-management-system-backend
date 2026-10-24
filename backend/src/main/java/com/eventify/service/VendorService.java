package com.eventify.service;

import com.eventify.model.ServiceType;
import com.eventify.model.Stall;
import com.eventify.model.Vendor;
import com.eventify.repository.ServiceTypeRepository;
import com.eventify.repository.StallRepository;
import com.eventify.repository.VendorRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VendorService {
    private final VendorRepository vendors;
    private final StallRepository stalls;
    private final ServiceTypeRepository serviceTypes;

    public VendorService(VendorRepository vendors, StallRepository stalls, ServiceTypeRepository serviceTypes) {
        this.vendors = vendors;
        this.stalls = stalls;
        this.serviceTypes = serviceTypes;
    }

    public List<Vendor> getAll() {
        return vendors.findAll();
    }

    public Optional<Vendor> getById(UUID id) {
        return vendors.findById(id);
    }

    public String save(Vendor vendor) {
        attachExistingRelations(vendor);
        vendors.save(vendor);
        return "Vendor saved successfully";
    }

    public String update(UUID id, Vendor input) {
        return vendors.findById(id).map(existing -> {
            existing.setVendorName(input.getVendorName());
            existing.setContactPerson(input.getContactPerson());
            existing.setPhone(input.getPhone());
            existing.setStall(resolveStall(input.getStall()));
            existing.setServiceTypes(resolveServiceTypes(input.getServiceTypes()));
            vendors.save(existing);
            return "Vendor updated successfully";
        }).orElse("The vendor with that id does not exist");
    }

    public String delete(UUID id) {
        return vendors.findById(id).map(existing -> {
            vendors.delete(existing);
            return "Vendor deleted successfully";
        }).orElse("The vendor with that id does not exist");
    }
    private void attachExistingRelations(Vendor vendor) {
        vendor.setStall(resolveStall(vendor.getStall()));
        vendor.setServiceTypes(resolveServiceTypes(vendor.getServiceTypes()));
    }

    private Stall resolveStall(Stall stall) {
        if (stall == null || stall.getId() == null) {
            return null;
        }
        return stalls.findById(stall.getId()).orElse(null);
    }

    private List<ServiceType> resolveServiceTypes(List<ServiceType> input) {
        if (input == null) {
            return new ArrayList<>();
        }
        List<UUID> ids = input.stream()
                .map(ServiceType::getId)
                .filter(id -> id != null)
                .toList();
        return new ArrayList<>(serviceTypes.findAllById(ids));
    }
}
