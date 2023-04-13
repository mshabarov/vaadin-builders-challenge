package org.teamhq.views.event.dialog;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.teamhq.data.entity.*;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.teamhq.data.entity.Meal;
import org.teamhq.data.entity.MealChoice;
import org.teamhq.data.entity.RsvpAnswer;
import org.teamhq.data.entity.Vendor;
import org.teamhq.data.service.MealChoiceService;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportDialog extends Dialog {
    private final MealChoiceService mealChoiceService;

    public ReportDialog(MealChoiceService mealChoiceService) {
        this.mealChoiceService = mealChoiceService;
    }

    public void open(Meal meal) {
        initUI(meal);
        open();
    }

    private void initUI(Meal meal) {
        initHeader(meal);
        List<MealChoice> mealChoicesByMeal = mealChoiceService.getMealChoicesByMeal(meal)
                .stream().filter(mealChoice -> RsvpAnswer.YES.equals(mealChoice.getAnswer()))
                .toList();

        if (mealChoicesByMeal == null || mealChoicesByMeal.isEmpty()) {
            addNoAttendeesLayout();
        } else {
            if (meal.getVendors().size() < 2) {
                Vendor vendor = meal.getVendors().isEmpty() ? null : meal.getVendors().iterator().next();
                addAttendeeCountLayout(mealChoicesByMeal, vendor);
            } else {
                addChoicesLayout(mealChoicesByMeal);
            }
        }
    }

    private void initHeader(Meal meal) {
        DateTimeFormatter dateFormatter =
                DateTimeFormatter.ofPattern("d.M.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String dateTime = meal.getEvent().getStartDateTime()
                .format(dateFormatter) +
                " " +
                meal.getStartTime().format(timeFormatter) + " - " +
                meal.getEndTime().format(timeFormatter);

        this.setHeaderTitle("Food Report for " + meal.getName() +
                " (" + dateTime + ")");
    }

    private void addNoAttendeesLayout() {
        add(new Span("No attendees for this meal."));
    }

    private void addAttendeeCountLayout(List<MealChoice> choices, Vendor vendor) {
        VerticalLayout layout = new VerticalLayout();

        String title;
        if (vendor == null) {
            title = choices.size() + " people attending";
        } else {
            title = vendor.getName() + " - " + choices.size() + " people";
        }
        layout.add(new H1(title));


        List<MealChoice> exceptions = new ArrayList<>();
        for (MealChoice choice : choices) {
            String globalException = choice.getUser().getComment();
            String localException = choice.getComment();
            if ((globalException != null && !"".equals(globalException.trim())) ||
                    (localException != null && !"".equals(localException.trim()))) {
                exceptions.add(choice);
            }
        }

        if (exceptions.size() > 0) {
            Icon warningIcon = VaadinIcon.WARNING.create();
            warningIcon.addClassName("warning-sign");
            layout.add(warningIcon);
            exceptions.stream().map(this::createExceptionSpan).forEach(layout::add);
        }

        add(layout);
    }

    private void addChoicesLayout(List<MealChoice> choices) {
        VerticalLayout layout = new VerticalLayout();

        Map<Vendor,Integer> vendorCounts = new HashMap<>();
        Map<Vendor,List<MealChoice>> vendorExceptions = new HashMap<>();

        for (MealChoice choice : choices) {
            String globalException = choice.getUser().getComment();
            String localException = choice.getComment();
            int count =
                    vendorCounts.getOrDefault(choice.getVendor(), 0);
            count++;
            vendorCounts.put(choice.getVendor(), count);
            if ((globalException != null && !"".equals(globalException.trim())) ||
                    (localException != null && !"".equals(localException.trim()))) {
                List<MealChoice> vendorChoices =
                        vendorExceptions.getOrDefault(choice.getVendor(),
                                new ArrayList<>());
                vendorChoices.add(choice);
                vendorExceptions.put(choice.getVendor(), vendorChoices);
            }
        }

        for (Vendor vendor : vendorCounts.keySet()) {
            int count = vendorCounts.get(vendor);
            H1 vendorTitle = new H1(vendor.getName() + " - " + count + " " +
                    "people");
            layout.add(vendorTitle);

            List<MealChoice> vendorChoices = vendorExceptions.get(vendor);
            if (vendorChoices != null && vendorChoices.size() > 0) {
                Icon warningIcon = VaadinIcon.WARNING.create();
                warningIcon.addClassName("warning-sign");
                layout.add(warningIcon);
                vendorChoices.stream().map(this::createExceptionSpan).forEach(layout::add);
            }
        }

        add(layout);
    }

    private Span createExceptionSpan(MealChoice choice) {
        String globalException = choice.getUser().getComment();
        String localException = choice.getComment();
        StringBuilder builder = new StringBuilder();
        if (globalException != null) builder.append(globalException).append(" ");
        if (localException != null) builder.append(localException);
        return new Span(choice.getUser().getName() + " - " +
                builder);
    }
}
