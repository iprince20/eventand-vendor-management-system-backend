package com.eventify.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "stall")
public class Stall {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int stallNumber;
    private String name;

    protected Stall() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getStallNumber() {
        return stallNumber;
    }

    public void setStallNumber(int stallNumber) {
        this.stallNumber = stallNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
