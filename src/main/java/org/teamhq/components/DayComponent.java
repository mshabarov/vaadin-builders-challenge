package org.teamhq.components;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.teamhq.stubs.MealStub;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class DayComponent extends VerticalLayout {

    private Div titleComponent;
    private Button addButton;

    private int counter = 0;

    private final int MAX_MEALS = 5;

    private static final int MEALS_HEIGHT = 200;

    private VerticalLayout mealsContainer;

    public DayComponent(String title) {
        Icon addIcon = new Icon(VaadinIcon.PLUS);
        addButton = new Button(addIcon);
        addButton.addClickListener(click -> {
            LocalDateTime from = LocalDateTime.now().plusHours(counter++);
            LocalDateTime to = from.plusMinutes(30);
            MealStub meal = new MealStub(from, to);

            addMeal(meal);
        });

        addButton.setWidthFull();

        titleComponent = new Div();
        titleComponent.setText(title);

        mealsContainer = new VerticalLayout();
        mealsContainer.setAlignItems(Alignment.CENTER);
        mealsContainer.setHeight(MEALS_HEIGHT + "px");

        add(titleComponent, addButton, mealsContainer);

        getStyle().set("border", "dashed 2px green");
        setWidth("200px");
        setHeight("400px");
        getStyle().set("border-radius", "5px");
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
}
