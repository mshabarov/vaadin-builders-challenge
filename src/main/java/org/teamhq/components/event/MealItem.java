package org.teamhq.components.event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.teamhq.data.entity.Meal;
import org.teamhq.data.repository.MealChoiceRepository;
import org.teamhq.data.repository.MealRepository;
import org.teamhq.data.repository.VendorRepository;
import org.teamhq.views.event.dialog.MealDialog;
import org.teamhq.views.event.dialog.ReportDialog;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class MealItem extends Div {

    public static final int HEIGHT = 50;

    private final Meal meal;

    public MealItem(VendorRepository vendorRepository,
                    MealRepository mealRepository,
                    MealChoiceRepository mealChoiceRepository, Meal meal,
                    boolean confirmed) {
        this.meal = meal;
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
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if (authorities.stream().anyMatch(auth -> auth.getAuthority().contains(
                "ADMIN"))) {
            HorizontalLayout buttonLayout = new HorizontalLayout();
            buttonLayout.addClassName("actions");

            Button editButton = new Button(new Icon(VaadinIcon.PENCIL),
                    e -> {
                        // Show edit dialog
                        MealDialog mealDialog = new MealDialog(vendorRepository, mealRepository,
                                meal.getStartTime().toLocalDate(), meal, m -> {
                        });
                        mealDialog.open();
                    });
            editButton.getElement().setAttribute("aria-label", "Edit meal");
            buttonLayout.add(editButton);

            Button reportButton = new Button(new Icon(VaadinIcon.LIST_OL),
                    e -> {
                        ReportDialog reportDialog = new ReportDialog(mealChoiceRepository);
                        reportDialog.open(meal);
                    });
            reportButton.getElement().setAttribute("aria-label", "Show report");
            buttonLayout.add(reportButton);

            Button deleteButton = new Button(new Icon(VaadinIcon.CLOSE),
                    e -> {
                        // Show delete confirm dialog
                        // TODO
                        e.getSource().findAncestor(MealItem.class).removeFromParent();
                    });
            deleteButton.getElement().setAttribute("aria-label", "Delete meal");
            buttonLayout.add(deleteButton);

            layout.add(buttonLayout);
        }

        add(layout);

        setWidthFull();
    }

    public LocalDateTime getFrom() {
        return meal.getStartTime();
    }

    public LocalDateTime getTo() {
        return meal.getEndTime();
    }
}
