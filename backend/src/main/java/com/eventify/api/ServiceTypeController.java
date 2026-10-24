package com.eventify.api;

import com.eventify.model.ServiceType;
import com.eventify.service.ServiceTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/service-types")
public class ServiceTypeController {
    private final ServiceTypeService serviceTypes;

    public ServiceTypeController(ServiceTypeService serviceTypes) {
        this.serviceTypes = serviceTypes;
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody ServiceType serviceType) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceTypes.save(serviceType));
    }

    @GetMapping("/all")
    public List<ServiceType> getAll() {
        return serviceTypes.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        var result = serviceTypes.getById(id);
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("The service type with that id does not exist");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable UUID id, @RequestBody ServiceType serviceType) {
        String message = serviceTypes.update(id, serviceType);
        if (message.equals("The service type with that id does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = serviceTypes.delete(id);
        if (message.equals("The service type with that id does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }
        return ResponseEntity.ok(message);
    }
}
