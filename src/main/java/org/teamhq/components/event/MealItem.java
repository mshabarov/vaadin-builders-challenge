package org.teamhq.components.event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Optional;

import com.vaadin.flow.component.UI;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.teamhq.data.entity.Meal;
import org.teamhq.data.entity.MealChoice;
import org.teamhq.data.entity.RsvpAnswer;
import org.teamhq.data.repository.VendorRepository;
import org.teamhq.data.service.MealChoiceService;
import org.teamhq.data.service.MealService;
import org.teamhq.security.AuthenticatedUser;
import org.teamhq.views.event.dialog.AttendanceDialog;
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
                    MealService mealService,
                    MealChoiceService mealChoiceService,
                    Meal meal,
                    boolean confirmed,
                    AuthenticatedUser authenticatedUser) {
        this.meal = meal;

        VerticalLayout layout = new VerticalLayout();
        determineClassNames(layout, confirmed);

        AttendanceDialog attendanceDialog = new AttendanceDialog(authenticatedUser, mealChoiceService, mealService,
                mealChoice -> determineClassNames(layout, RsvpAnswer.YES.equals(Optional.ofNullable(mealChoice).map(MealChoice::getAnswer).orElse(RsvpAnswer.NO))));
        this.addClickListener(e -> {
            // Show attendance dialog
            attendanceDialog.open(meal);
        });

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
                        MealDialog mealDialog = new MealDialog(vendorRepository, mealService,
                                meal.getStartTime().toLocalDate(), meal, m -> {
                        });
                        mealDialog.open();
                        UI.getCurrent().getPage().executeJs("e.stopPropagation();");
                    });
            editButton.getElement().addEventListener("click", ignore -> {}).addEventData("event.stopPropagation()");
            editButton.getElement().setAttribute("aria-label", "Edit meal");
            buttonLayout.add(editButton);

            Button reportButton = new Button(new Icon(VaadinIcon.LIST_OL),
                    e -> {
                        ReportDialog reportDialog = new ReportDialog(mealChoiceService);
                        reportDialog.open(meal);
                    });
            reportButton.getElement().addEventListener("click", ignore -> {}).addEventData("event.stopPropagation()");
            reportButton.getElement().setAttribute("aria-label", "Show report");
            buttonLayout.add(reportButton);

            Button deleteButton = new Button(new Icon(VaadinIcon.CLOSE),
                    e -> {
                        // Show delete confirm dialog
                        // TODO
                        e.getSource().findAncestor(MealItem.class).removeFromParent();
                    });
            deleteButton.getElement().addEventListener("click", ignore -> {}).addEventData("event.stopPropagation()");
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

    private void determineClassNames(VerticalLayout layout, boolean confirmed) {
        layout.removeClassNames("card", "confirmed", "unconfirmed");

        layout.addClassName("card");
        if (confirmed) {
            layout.addClassName("confirmed");
        } else {
            layout.addClassNames("unconfirmed");
        }
    }
}
