package org.teamhq.components;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.teamhq.components.event.MealItem;
import org.teamhq.data.entity.Event;
import org.teamhq.data.repository.MealRepository;
import org.teamhq.data.repository.VendorRepository;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.teamhq.data.repository.MealChoiceRepository;
import org.teamhq.views.event.dialog.MealDialog;

public class DayComponent extends VerticalLayout {

    private LocalDate date;
    private Button addButton;

    private int counter = 0;

    private final int MAX_MEALS = 5;

    private static final int MEALS_HEIGHT = 600;

    private VerticalLayout mealsContainer;

    private VendorRepository vendorRepository;

    private MealRepository mealRepository;

    private MealChoiceRepository mealChoiceRepository;

    public DayComponent(VendorRepository vendorRepository,
                        MealRepository mealRepository, MealChoiceRepository mealChoiceRepository, LocalDate date,
                        Event event,
                        Collection<MealItem> meals) {
        this.vendorRepository = vendorRepository;
        this.mealRepository = mealRepository;
        this.mealChoiceRepository = mealChoiceRepository;
        this.date = date;
        Icon addIcon = new Icon(VaadinIcon.PLUS);
        addButton = new Button(addIcon);
        addButton.addClickListener(click -> {
            MealDialog mealDialog = new MealDialog(vendorRepository,
                    mealRepository, event, date, m -> {
                MealItem mealItem = new MealItem(mealChoiceRepository, m, false);
                addMeal(mealItem);
            });
            mealDialog.open();
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
