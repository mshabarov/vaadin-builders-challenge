package org.teamhq.views.event;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.teamhq.components.DayComponent;
import org.teamhq.components.event.MealItem;
import org.teamhq.data.entity.Event;
import org.teamhq.data.entity.Meal;
import org.teamhq.data.repository.EventRepository;
import org.teamhq.data.repository.MealRepository;
import org.teamhq.data.repository.VendorRepository;
import org.teamhq.views.MainLayout;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@PageTitle("Event")
@Route(value = "event", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class EventView extends HorizontalLayout implements HasUrlParameter<Long> {

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private MealRepository mealRepository;

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long parameter) {
        removeAll();
//        LocalDate now = LocalDate.now();
//
//        ArrayList<MealItem> meals = new ArrayList<>();
//        Meal meal = new Meal();
//        meal.setStartTime(LocalTime.now());
//        meal.setEndTime(LocalTime.now().plusHours(1));
//        MealItem mealStub = new MealItem(meal, false);
//        meals.add(mealStub);
//
//        DayComponent dayComponent = new DayComponent(now, meals);
//        DayComponent dayComponent2 = new DayComponent(now, meals);
//        DayComponent dayComponent3 = new DayComponent(now, meals);
//        add(dayComponent, dayComponent2, dayComponent3);

        Optional<Event> eventEntity;

        Event eventObject = new Event();
        eventObject.setName("My Test Event");
        eventObject.setDescription("Test Event");
        eventObject.setStartDateTime(LocalDateTime.now());
        eventObject.setEndDateTime(LocalDateTime.now().plusDays(4));
        eventEntity = Optional.of(eventObject);

//        if (parameter == null) {
//            eventEntity = Optional.ofNullable(eventRepository.findAll().get(0));
//        } else {
//            eventEntity = eventRepository.findById(parameter);
//        }
        if (eventEntity.isPresent()) {
            List<Meal> meals =
                    mealRepository.getAllMealsByEventId(eventEntity.get().getId());
            LocalDateTime startDateTime = eventEntity.get().getStartDateTime();
            LocalDateTime endDateTime = eventEntity.get().getEndDateTime();

            long daysBetween = Duration.between(startDateTime, endDateTime).toDays();

            for (int i = 0; i < daysBetween; i++) {
                int finalI = i;
                List<Meal> filteredMeals = meals.stream().filter(m -> m.getStartTime().getDayOfYear() == startDateTime.getDayOfYear() + finalI).collect(Collectors.toList());

                Collection<MealItem> mealItems = new ArrayList<>();
                filteredMeals.forEach(m -> {
                    MealItem mealItem = new MealItem(m, false);
                    mealItems.add(mealItem);
                });

                DayComponent dayComponent =
                        new DayComponent(vendorRepository, mealRepository,
                                startDateTime.plusDays(finalI).toLocalDate(),
                                eventEntity.get(), mealItems);
                add(dayComponent);
            }

        }


        addClassName("board");
    }
}
