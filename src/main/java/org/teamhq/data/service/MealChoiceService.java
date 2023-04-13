package org.teamhq.data.service;

import org.springframework.stereotype.Service;
import org.teamhq.data.entity.Meal;
import org.teamhq.data.entity.MealChoice;
import org.teamhq.data.entity.User;
import org.teamhq.data.repository.MealChoiceRepository;

import java.util.List;

@Service
public class MealChoiceService {

    private final MealChoiceRepository mealChoiceRepository;

    public MealChoiceService(MealChoiceRepository mealChoiceRepository) {
        this.mealChoiceRepository = mealChoiceRepository;
    }


    public MealChoice getMealChoiceByMealAndUser(Meal meal, User user) {
        return mealChoiceRepository.getMealChoiceByMealAndUser(meal, user);
    }

    public List<MealChoice> getMealChoicesByMeal(Meal meal) {
        return mealChoiceRepository.getMealChoicesByMeal(meal);
    }

    public void delete(MealChoice mealChoice) {
        mealChoiceRepository.delete(mealChoice);
    }

    public MealChoice save(MealChoice mealChoice) {
        return mealChoiceRepository.save(mealChoice);
    }
}
