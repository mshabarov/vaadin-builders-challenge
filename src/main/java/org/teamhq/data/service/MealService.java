package org.teamhq.data.service;

import org.springframework.stereotype.Service;
import org.teamhq.data.entity.Meal;
import org.teamhq.data.repository.MealRepository;

import java.util.List;
import java.util.Optional;

@Service
public class MealService {

    private final MealRepository repository;

    public MealService(MealRepository repository) {
        this.repository = repository;
    }

    public Optional<Meal> findById(Long id) {
        return repository.findById(id);
    }

    public Meal getById(Long id) {
        return repository.getPopulatedMealById(id);
    }

    public List<Meal> getAllMealsByEventId(Long eventId) {
        return repository.getAllMealsByEventId(eventId);
    }

    public Meal save(Meal meal) {
        return repository.save(meal);
    }

    public void delete(Meal meal) {
        repository.delete(meal);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
