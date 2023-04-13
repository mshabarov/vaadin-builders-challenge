package org.teamhq.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.teamhq.components.event.MealItem;
import org.teamhq.data.entity.Event;
import org.teamhq.data.entity.MealChoice;
import org.teamhq.data.entity.RsvpAnswer;
import org.teamhq.data.entity.User;
import org.teamhq.data.repository.VendorRepository;
import org.teamhq.data.service.MealChoiceService;
import org.teamhq.data.service.MealService;
import org.teamhq.security.AuthenticatedUser;
import org.teamhq.views.event.dialog.MealDialog;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DayComponent extends VerticalLayout {

    private LocalDate date;
    private Button addButton;

    private int counter = 0;

    private final int MAX_MEALS = 5;

    private static final int MEALS_HEIGHT = 600;

    private VerticalLayout mealsContainer;

    private final VendorRepository vendorRepository;

    private final MealService mealService;

    private final MealChoiceService mealChoiceService;

    private final AuthenticatedUser authenticatedUser;

    public DayComponent(VendorRepository vendorRepository,
                        MealService mealService, MealChoiceService mealChoiceService, LocalDate date,
                        Event event,
                        Collection<MealItem> meals,
                        AuthenticatedUser authenticatedUser) {
        this.vendorRepository = vendorRepository;
        this.mealService = mealService;
        this.mealChoiceService = mealChoiceService;
        this.authenticatedUser = authenticatedUser;
        this.date = date;
        Icon addIcon = new Icon(VaadinIcon.PLUS);
        addButton = new Button(addIcon);
        User currentUser = authenticatedUser.require();
        addButton.addClickListener(click -> {
            MealDialog mealDialog = new MealDialog(vendorRepository,
                    // TODO: remove callback
                    mealService, event, date, m -> {
                var mealChoice = mealChoiceService.getMealChoiceByMealAndUser(m, currentUser);
                MealItem mealItem = new MealItem(vendorRepository,
                        mealService,
                        mealChoiceService,
                        m,
                        RsvpAnswer.YES.equals(Optional.ofNullable(mealChoice).map(MealChoice::getAnswer).orElse(RsvpAnswer.NO)),
                        authenticatedUser);
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

            if (mealsContainer.getComponentCount() == MAX_MEALS) {
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
