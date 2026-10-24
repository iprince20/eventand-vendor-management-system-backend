package com.eventify.service;

import com.eventify.model.Organizer;
import com.eventify.repository.OrganizerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrganizerService {
    private final OrganizerRepository organizers;

    public OrganizerService(OrganizerRepository organizers) {
        this.organizers = organizers;
    }

    public List<Organizer> getAll() {
        return organizers.findAll();
    }

    public Optional<Organizer> getById(UUID id) {
        return organizers.findById(id);
    }

    public String save(Organizer organizer) {
        organizers.save(organizer);
        return "Organizer saved successfully";
    }

    public String update(UUID id, Organizer input) {
        return organizers.findById(id).map(existing -> {
            existing.setFirstName(input.getFirstName());
            existing.setLastName(input.getLastName());
            existing.setOrganizerCode(input.getOrganizerCode());
            existing.setPhone(input.getPhone());
            existing.setEmail(input.getEmail());
            organizers.save(existing);
            return "Organizer updated successfully";
        }).orElse("The organizer with that id does not exist");
    }

    public String delete(UUID id) {
        return organizers.findById(id).map(existing -> {
            organizers.delete(existing);
            return "Organizer deleted successfully";
        }).orElse("The organizer with that id does not exist");
    }
}
