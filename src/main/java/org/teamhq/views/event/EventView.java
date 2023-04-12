package org.teamhq.views.event;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.PermitAll;
import org.teamhq.components.DayComponent;
import org.teamhq.data.entity.Meal;
import org.teamhq.views.MainLayout;

@PageTitle("Event")
@Route(value = "event", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class EventView extends HorizontalLayout {

    public EventView() {
        DayComponent dayComponent = new DayComponent("Test title");
        add(dayComponent);
    }

}
