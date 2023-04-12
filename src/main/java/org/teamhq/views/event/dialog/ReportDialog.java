package org.teamhq.views.event.dialog;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.teamhq.data.entity.Meal;
import org.teamhq.data.entity.MealChoice;
import org.teamhq.data.entity.Vendor;
import org.teamhq.data.repository.MealChoiceRepository;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@Component
public class ReportDialog extends Dialog {
    private final MealChoiceRepository mealChoiceRepository;
    private Meal meal;

    public ReportDialog(MealChoiceRepository mealChoiceRepository) {
        this.mealChoiceRepository = mealChoiceRepository;
    }

    public void open(Meal meal) {

    }

    private void initUI(Meal meal) {
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

        VerticalLayout layout = new VerticalLayout();

        Map<Vendor,Integer> vendorCounts = new HashMap<>();
        Map<Vendor,List<MealChoice>> vendorExceptions = new HashMap<>();
        List<MealChoice> choices =
                mealChoiceRepository.getMealChoicesByMeal(meal);
        if (choices != null) {
            for (MealChoice choice : choices) {
                String globalException = choice.getUser().getComment();
                String localException = choice.getComment();
                if (globalException == null && localException == null) {
                    int count =
                            vendorCounts.getOrDefault(choice.getVendor(), 0);
                    count++;
                    vendorCounts.put(choice.getVendor(), count);
                } else {
                    List<MealChoice> vendorChoices =
                            vendorExceptions.getOrDefault(choice.getVendor(),
                                    new ArrayList<>());
                    vendorChoices.add(choice);
                    vendorExceptions.put(choice.getVendor(), vendorChoices);
                }
            }
        }

        for (Vendor vendor : vendorCounts.keySet()) {
            int count = vendorCounts.get(vendor);
            H1 vendorTitle = new H1(vendor.getName() + " - " + count + " " +
                    "people");
            layout.add(vendorTitle);

            List<MealChoice> vendorChoices = vendorExceptions.get(vendor);
            if (vendorChoices.size() > 0) {
                layout.add(new Icon(VaadinIcon.WARNING));

                for (MealChoice choice : vendorChoices) {
                    String globalException = choice.getUser().getComment();
                    String localException = choice.getComment();
                    StringBuilder builder = new StringBuilder();
                    if (globalException != null) builder.append(globalException).append(" ");
                    if (localException != null) builder.append(localException);
                    Span exceptionSpan =
                            new Span(choice.getUser().getName() + " - " +
                                    builder);
                    layout.add(exceptionSpan);
                }
            }
        }

        add(layout);
    }
}
