package org.teamhq.views.event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import jakarta.annotation.security.PermitAll;
import org.teamhq.components.DayComponent;
import org.teamhq.stubs.MealStub;
import org.teamhq.views.MainLayout;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@PageTitle("Event")
@Route(value = "event", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class EventView extends HorizontalLayout {

    public EventView() {
        LocalDate now = LocalDate.now();

        ArrayList<MealStub> meals = new ArrayList<>();
        MealStub mealStub = new MealStub(LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        meals.add(mealStub);

        DayComponent dayComponent = new DayComponent(now, meals);
        add(dayComponent);
    }

}
