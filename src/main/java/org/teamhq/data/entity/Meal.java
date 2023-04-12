package org.teamhq.data.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotEmpty;

@Entity
public class Meal {
    @Id
    private Number id;

    @ManyToOne
    private Event event;

    @NotEmpty
    private String name;

    private String description;

    @Temporal(TemporalType.TIME)
    private LocalTime startTime;

    @Temporal(TemporalType.TIME)
    private LocalTime endTime;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime freezeDateTime;

    public Number getId() {
        return id;
    }

    public void setId(Number id) {
        this.id = id;
    }

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

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startDate) {
        this.startTime = startDate;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endDate) {
        this.endTime = endDate;
    }

    public LocalDateTime getFreezeDateTime() {
        return freezeDateTime;
    }

    public void setFreezeDateTime(LocalDateTime freezeDateTime) {
        this.freezeDateTime = freezeDateTime;
    }
}
