package org.teamhq.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.teamhq.data.entity.Meal;
import org.teamhq.data.entity.MealChoice;
import org.teamhq.data.entity.User;

import java.util.List;

public interface MealChoiceRepository extends JpaRepository<MealChoice, Long>, JpaSpecificationExecutor<MealChoice> {

    MealChoice getMealChoiceByMealAndUser(Meal meal, User user);

    List<MealChoice> getMealChoicesByMeal(Meal meal);
}
