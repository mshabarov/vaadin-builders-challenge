package org.teamhq.components;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.teamhq.stubs.MealStub;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class DayComponent extends VerticalLayout {

    private LocalDate date;
    private Button addButton;

    private int counter = 0;

    private final int MAX_MEALS = 5;

    private static final int MEALS_HEIGHT = 200;

    private VerticalLayout mealsContainer;

    public DayComponent(LocalDate date, Collection<MealStub> meals) {
        this.date = date;
        Icon addIcon = new Icon(VaadinIcon.PLUS);
        addButton = new Button(addIcon);
        addButton.addClickListener(click -> {
            LocalDateTime from = LocalDateTime.now().plusHours(counter++);
            LocalDateTime to = from.plusMinutes(30);
            MealStub meal = new MealStub(from, to);

            addMeal(meal);
        });

        addButton.setWidthFull();

        Div titleComponent = new Div();
        titleComponent.setText(date.toString());

        mealsContainer = new VerticalLayout();
        mealsContainer.setAlignItems(Alignment.CENTER);
        mealsContainer.setHeight(MEALS_HEIGHT + "px");
        meals.forEach(this::addMeal);

        setWidth("200px");
        setClassName("day-component");
        add(titleComponent, addButton, mealsContainer);
    }

    public void addMeal(MealStub meal) {
        if (mealsContainer.getComponentCount() > 0) {
            List<Component> meals = mealsContainer.getChildren().collect(Collectors.toList());
            meals.add(meal);

            meals.sort(Comparator.comparing(one -> ((MealStub) one).getFrom()));

            if (meals.size() * MealStub.HEIGHT > MEALS_HEIGHT) {
                int newSize = MEALS_HEIGHT / meals.size();
                meals.forEach(m -> ((MealStub) m).setHeight(newSize + "px") );
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

    public void removeMeal(MealStub meal) {
        mealsContainer.remove(meal);
    }
}
