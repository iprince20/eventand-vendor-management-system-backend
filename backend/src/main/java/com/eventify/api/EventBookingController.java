package com.eventify.api;

import com.eventify.model.BookingStatus;
import com.eventify.model.EventBooking;
import com.eventify.service.EventBookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/event-bookings")
public class EventBookingController {
    private final EventBookingService eventBookings;

    public EventBookingController(EventBookingService eventBookings) {
        this.eventBookings = eventBookings;
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody EventBooking booking) {
        String message = eventBookings.save(booking);
        if (message.equals("The vendor with that id does not exist")
                || message.equals("The organizer with that id does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        } else if (message.equals("Event booking date cannot be in the past")) {
            return ResponseEntity.badRequest().body(message);
        } else if (message.equals("Vendor is already booked for an event on that date")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
        } else if (message.equals("Event booking saved successfully")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        }
        return ResponseEntity.badRequest().body(message);
    }

    @GetMapping("/all")
    public List<EventBooking> getAll() {
        return eventBookings.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        var result = eventBookings.getById(id);
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("The event booking with that id does not exist");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable UUID id, @RequestBody EventBooking booking) {
        String message = eventBookings.update(id, booking);
        if (message.equals("The event booking with that id does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = eventBookings.delete(id);
        if (message.equals("The event booking with that id does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }
        return ResponseEntity.ok(message);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateStatus(@PathVariable UUID id, @RequestParam BookingStatus status) {
        String message = eventBookings.updateStatus(id, status);
        if (message.equals("The event booking with that id does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        } else if (message.startsWith("Cannot change status from ")) {
            return ResponseEntity.badRequest().body(message);
        }
        return ResponseEntity.ok(message);
    }

    @GetMapping("/by-status")
    public List<EventBooking> findByStatus(@RequestParam BookingStatus status) {
        return eventBookings.findByStatus(status);
    }

    @GetMapping("/between")
    public List<EventBooking> findBetween(@RequestParam String start, @RequestParam String end) {
        return eventBookings.findBetween(LocalDate.parse(start), LocalDate.parse(end));
    }
}
