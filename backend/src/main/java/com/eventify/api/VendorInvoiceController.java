package com.eventify.api;

import com.eventify.model.VendorInvoice;
import com.eventify.service.VendorInvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vendor-invoices")
public class VendorInvoiceController {
    private final VendorInvoiceService invoices;

    public VendorInvoiceController(VendorInvoiceService invoices) {
        this.invoices = invoices;
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestParam UUID eventBookingId,
                                       @RequestBody VendorInvoice invoice) {
        String message = invoices.save(eventBookingId, invoice);
        if (message.equals("The event booking with that id does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        } else if (message.equals("Vendor invoices are only for DELIVERED event bookings")) {
            return ResponseEntity.badRequest().body(message);
        } else if (message.equals("Vendor invoice saved successfully")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        }
        return ResponseEntity.badRequest().body(message);
    }

    @GetMapping("/organizer/{organizerCode}")
    public List<VendorInvoice> findByOrganizer(@PathVariable String organizerCode) {
        return invoices.findByOrganizerCode(organizerCode);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = invoices.delete(id);
        if (message.equals("The vendor invoice with that id does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }
        return ResponseEntity.ok(message);
    }
}
