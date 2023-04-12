package org.teamhq.views.event;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import jakarta.annotation.security.PermitAll;
import org.teamhq.components.DayComponent;
import org.teamhq.components.event.MealItem;
import org.teamhq.data.entity.Meal;
import org.teamhq.views.MainLayout;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@PageTitle("Event")
@Route(value = "event", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class EventView extends HorizontalLayout implements HasUrlParameter<String> {

    private String eventId;

    public EventView() {
        LocalDate now = LocalDate.now();

        ArrayList<MealItem> meals = new ArrayList<>();
        Meal meal = new Meal();
        meal.setStartTime(LocalTime.now());
        meal.setEndTime(LocalTime.now().plusHours(1));
        MealItem mealStub = new MealItem(meal, false);
        meals.add(mealStub);

        DayComponent dayComponent = new DayComponent(now, meals);
        DayComponent dayComponent2 = new DayComponent(now, meals);
        DayComponent dayComponent3 = new DayComponent(now, meals);
        add(dayComponent, dayComponent2, dayComponent3);

        addClassName("board");
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        this.eventId = parameter;
    }
}
