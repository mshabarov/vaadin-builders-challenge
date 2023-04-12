package org.teamhq.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.teamhq.data.entity.Meal;

import java.util.List;

public interface MealRepository extends JpaRepository<Meal, Long>, JpaSpecificationExecutor<Meal> {

    @Query("""
            SELECT m
              FROM Meal m
              JOIN FETCH Vendor v
             WHERE m.event.id = :eventId
            """)
    List<Meal> getAllMealsByEventId(Long eventId);
}
