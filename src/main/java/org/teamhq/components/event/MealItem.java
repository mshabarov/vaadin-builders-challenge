package org.teamhq.components.event;

import java.time.format.DateTimeFormatter;

import org.teamhq.data.entity.Meal;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class MealItem extends Div {
    public MealItem(Meal meal, boolean confirmed) {
        this.addClickListener(e -> {
            // Show attendance dialog
        });

        VerticalLayout layout = new VerticalLayout();

        layout.addClassName("card");
        if (confirmed) {
            layout.addClassName("confirmed");
        } else {
            layout.addClassNames("unconfirmed");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String time = meal.getStartTime().format(formatter) + " - " +
                meal.getEndTime().format(formatter);
        H3 timeHeading = new H3(time);
        layout.add(timeHeading);

        H1 titleHeading = new H1(meal.getName());
        layout.add(titleHeading);

        // If the current user has the Admin role
        if (true) {
            HorizontalLayout buttonLayout = new HorizontalLayout();
            buttonLayout.addClassName("actions");

            Button editButton = new Button(new Icon(VaadinIcon.PENCIL),
                    e -> {
                        // Show edit dialog
                    });
            editButton.getElement().setAttribute("aria-label", "Edit meal");
            buttonLayout.add(editButton);

            Button reportButton = new Button(new Icon(VaadinIcon.LIST_OL),
                    e -> {
                        // Show report dialog
                    });
            reportButton.getElement().setAttribute("aria-label", "Show report");
            buttonLayout.add(reportButton);

            Button deleteButton = new Button(new Icon(VaadinIcon.CLOSE),
                    e -> {
                        // Show delete confirm dialog
                    });
            deleteButton.getElement().setAttribute("aria-label", "Delete meal");
            buttonLayout.add(deleteButton);

            layout.add(buttonLayout);
        }

        add(layout);
    }
}
