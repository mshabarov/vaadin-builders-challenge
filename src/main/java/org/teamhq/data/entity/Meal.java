package org.teamhq.data.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

@Entity
public class Meal extends AbstractEntity {

    @NotEmpty
    private String name;

    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime startTime;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime endTime;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime freezeDateTime;

    @ManyToOne
    private Event event;

    @ManyToMany
    private Set<Vendor> vendors;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startDate) {
        this.startTime = startDate;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endDate) {
        this.endTime = endDate;
    }

    public LocalDateTime getFreezeDateTime() {
        return freezeDateTime;
    }

    public void setFreezeDateTime(LocalDateTime freezeDateTime) {
        this.freezeDateTime = freezeDateTime;
    }

    public Set<Vendor> getVendors() {
        return vendors;
    }

    public void setVendors(Set<Vendor> vendors) {
        this.vendors = vendors;
    }
}
