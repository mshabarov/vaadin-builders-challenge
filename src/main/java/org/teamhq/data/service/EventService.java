package org.teamhq.data.service;

import org.springframework.stereotype.Service;
import org.teamhq.data.entity.Event;
import org.teamhq.data.repository.EventRepository;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository repository;

    public EventService(EventRepository eventRepository) {
        this.repository = eventRepository;
    }

    public List<Event> findAll() {
        return repository.findAll();
    }

    public Optional<Event> findById(Long id) {
        return repository.findById(id);
    }

    public Event save(Event event) {
        return repository.save(event);
    }

    public void delete(Event event) {
        repository.delete(event);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
