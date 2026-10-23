package com.eventify.service;

import com.eventify.model.ServiceType;
import com.eventify.model.Stall;
import com.eventify.model.Vendor;
import com.eventify.repository.ServiceTypeRepository;
import com.eventify.repository.StallRepository;
import com.eventify.repository.VendorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public String save(Vendor vendor) {
        attachExistingRelations(vendor);
        vendors.save(vendor);
        return "Vendor saved successfully";
    }

    @Transactional
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

    public List<Vendor> findByServiceType(String name) {
        return vendors.findByServiceTypeName(name);
    }

    @Transactional
    public String addServiceType(UUID vendorId, UUID serviceTypeId) {
        Optional<Vendor> vendorResult = vendors.findById(vendorId);
        if (vendorResult.isEmpty()) {
            return "The vendor with that id does not exist";
        }
        Optional<ServiceType> serviceTypeResult = serviceTypes.findById(serviceTypeId);
        if (serviceTypeResult.isEmpty()) {
            return "That service type does not exist";
        }

        Vendor vendor = vendorResult.get();
        ServiceType serviceType = serviceTypeResult.get();
        if (vendor.getServiceTypes().contains(serviceType)) {
            return "The vendor already has that service type";
        }
        vendor.getServiceTypes().add(serviceType);
        vendors.save(vendor);
        return "Service type added to the vendor";
    }

    @Transactional
    public String moveToStall(UUID vendorId, int stallNumber) {
        Optional<Vendor> vendorResult = vendors.findById(vendorId);
        if (vendorResult.isEmpty()) {
            return "The vendor with that id does not exist";
        }
        Optional<Stall> stallResult = stalls.findByStallNumber(stallNumber);
        if (stallResult.isEmpty()) {
            return "The stall with that stallNumber does not exist";
        }

        Vendor vendor = vendorResult.get();
        Stall newStall = stallResult.get();
        boolean alreadyOccupiedByAnotherVendor = vendors.existsByStall(newStall)
                && (vendor.getStall() == null || !vendor.getStall().getId().equals(newStall.getId()));
        if (alreadyOccupiedByAnotherVendor) {
            return "That stall is taken by another vendor";
        }

        vendor.setStall(newStall);
        vendors.save(vendor);
        return "Stall changed successfully";
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
