package com.eventify.service;

import com.eventify.model.Stall;
import com.eventify.repository.StallRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StallService {
    private final StallRepository stalls;

    public StallService(StallRepository stalls) {
        this.stalls = stalls;
    }

    public List<Stall> getAll() {
        return stalls.findAll();
    }

    public Optional<Stall> getById(UUID id) {
        return stalls.findById(id);
    }

    public String save(Stall stall) {
        stalls.save(stall);
        return "Stall saved successfully";
    }

    public String update(UUID id, Stall input) {
        return stalls.findById(id).map(existing -> {
            existing.setStallNumber(input.getStallNumber());
            existing.setName(input.getName());
            stalls.save(existing);
            return "Stall updated successfully";
        }).orElse("The stall with that id does not exist");
    }

    public String delete(UUID id) {
        return stalls.findById(id).map(existing -> {
            stalls.delete(existing);
            return "Stall deleted successfully";
        }).orElse("The stall with that id does not exist");
    }

    public List<Stall> findFree() {
        return stalls.findFreeStalls();
    }

    public Optional<Stall> findByStallNumber(int stallNumber) {
        return stalls.findByStallNumber(stallNumber);
    }
}
