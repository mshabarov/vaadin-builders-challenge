package org.teamhq.components;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.teamhq.components.event.MealItem;
import org.teamhq.data.entity.Meal;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.teamhq.data.repository.MealChoiceRepository;

public class DayComponent extends VerticalLayout {

    private LocalDate date;
    private Button addButton;

    private int counter = 0;

    private final int MAX_MEALS = 5;

    private static final int MEALS_HEIGHT = 600;

    private VerticalLayout mealsContainer;

    public DayComponent(MealChoiceRepository mealChoiceRepository, LocalDate date, Collection<MealItem> meals) {
        this.date = date;
        Icon addIcon = new Icon(VaadinIcon.PLUS);
        addButton = new Button(addIcon);
        addButton.addClickListener(click -> {
            LocalTime from = LocalTime.now().plusHours(counter++);
            LocalTime to = from.plusMinutes(30);
            Meal newMeal = new Meal();
            newMeal.setStartTime(from);
            newMeal.setEndTime(to);
            MealItem meal = new MealItem(mealChoiceRepository, newMeal, false);

            addMeal(meal);
        });

        addButton.setWidthFull();

        Div titleComponent = new Div();
        titleComponent.getStyle().set("font-weight", "bold");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "dd MMMM yyyy");
        titleComponent.setText(date.format(formatter));

        mealsContainer = new VerticalLayout();
        mealsContainer.setAlignItems(Alignment.CENTER);
        mealsContainer.setHeight(MEALS_HEIGHT + "px");
        meals.forEach(this::addMeal);

        setClassName("day-component");
        add(titleComponent, addButton, mealsContainer);
    }

    public void addMeal(MealItem meal) {
        if (mealsContainer.getComponentCount() > 0) {
            List<Component> meals = mealsContainer.getChildren().collect(Collectors.toList());
            meals.add(meal);

            meals.sort(Comparator.comparing(one -> ((MealItem) one).getFrom()));

            if (meals.size() * MealItem.HEIGHT > MEALS_HEIGHT) {
                int newSize = MEALS_HEIGHT / meals.size();
                meals.forEach(m -> ((MealItem) m).setHeight(newSize + "px") );
            }

            mealsContainer.removeAll();
            mealsContainer.add(meals);

            if (mealsContainer.getComponentCount() == 5) {
                addButton.setEnabled(false);
            }
        } else {
            mealsContainer.add(meal);
        }
    }

    public void removeMeal(MealItem meal) {
        mealsContainer.remove(meal);
    }
}
