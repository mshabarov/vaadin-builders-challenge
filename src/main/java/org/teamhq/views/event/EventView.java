package org.teamhq.views.event;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.teamhq.components.DayComponent;
import org.teamhq.components.event.MealItem;
import org.teamhq.data.entity.*;
import org.teamhq.data.repository.VendorRepository;
import org.teamhq.data.service.EventService;
import org.teamhq.data.service.MealChoiceService;
import org.teamhq.data.service.MealService;
import org.teamhq.security.AuthenticatedUser;
import org.teamhq.views.HomeView;
import org.teamhq.views.MainLayout;
import org.teamhq.views.login.LoginView;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@PageTitle("Event")
@Route(value = "event", layout = MainLayout.class)
@PermitAll
public class EventView extends HorizontalLayout implements HasUrlParameter<Long> {

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private EventService eventService;

    @Autowired
    private MealService mealService;

    @Autowired
    private MealChoiceService mealChoiceService;

    @Autowired
    private AuthenticatedUser authenticatedUser;

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter Long parameter) {
        removeAll();
        addClassName("board");

        if (authenticatedUser.get().isEmpty()) {
            UI.getCurrent().navigate(LoginView.class);
            return;
        }

        if (parameter == null) {
            UI.getCurrent().navigate(HomeView.class);
            return;
        }

        Event eventEntity = eventService.findById(parameter).get();

        List<Meal> meals = mealService.getAllMealsByEventId(eventEntity.getId());
        LocalDateTime startDateTime = eventEntity.getStartDateTime();
        LocalDateTime endDateTime = eventEntity.getEndDateTime();

        User currentUser = authenticatedUser.require();

        long daysBetween = Duration.between(startDateTime, endDateTime).toDays() + 1;

        for (int i = 0; i < daysBetween; i++) {
            int finalI = i;
            List<Meal> filteredMeals = meals.stream().filter(m -> m.getStartTime().getDayOfYear() == startDateTime.getDayOfYear() + finalI).toList();

            Collection<MealItem> mealItems = new ArrayList<>();
            filteredMeals.forEach(m -> {
                var mealChoice = mealChoiceService.getMealChoiceByMealAndUser(m, currentUser);
                MealItem mealItem = new MealItem(vendorRepository,
                        mealService,
                        mealChoiceService,
                        m,
                        RsvpAnswer.YES.equals(Optional.ofNullable(mealChoice).map(MealChoice::getAnswer).orElse(RsvpAnswer.NO)),
                        authenticatedUser);
                mealItems.add(mealItem);
            });

            DayComponent dayComponent =
                    new DayComponent(vendorRepository, mealService, mealChoiceService,
                            startDateTime.plusDays(finalI).toLocalDate(),
                            eventEntity, mealItems, authenticatedUser);
            add(dayComponent);
        }
    }
}
