package org.teamhq.components.event;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.function.SerializableConsumer;
import org.teamhq.data.entity.Event;
import org.teamhq.data.service.EventService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class EventDialog extends Dialog {

    private TextField eventName;

    private TextArea description;

    private DatePicker start;

    private DatePicker end;

    private Button save;

    private Binder<Event> binder;

    private SerializableConsumer<Event> onSave;

    private EventService eventService;

    public EventDialog(EventService eventService,
                       SerializableConsumer<Event> onSave) {
        this.eventService = eventService;
        this.onSave = onSave;
        eventName = new TextField("Event name");
        description = new TextArea("Event Description");
        start = new DatePicker("Event Start date");
        end = new DatePicker("Event End date");
        save = new Button("Save", click -> saveEvent());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Event event = new Event();
        event.setStartDateTime(LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT));
        event.setEndDateTime(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT));

        binder = new Binder<>();
        binder.setBean(event);

        binder.forField(eventName).asRequired("Event name is mandatory")
                .bind(Event::getName, Event::setName);

        binder.forField(description).asRequired("Event Description is mandatory")
                .bind(Event::getDescription, Event::setDescription);

        binder.forField(start).asRequired("Start time is mandatory")
                .bind(d -> d.getStartDateTime().toLocalDate(),
                        (d, f) -> d.setStartDateTime(LocalDateTime.of(f,
                                LocalTime.MIDNIGHT)));

        binder.forField(end).asRequired("End time is mandatory")
                .bind(d -> d.getEndDateTime().toLocalDate(),
                        (d, f) -> d.setEndDateTime(LocalDateTime.of(f,
                                LocalTime.MIDNIGHT)));

        FormLayout formLayout = new FormLayout();

        formLayout.add(start, end, eventName, description);
        formLayout.setResponsiveSteps(
                // Use one column by default
                new FormLayout.ResponsiveStep("0", 1),
                // Use two columns, if buttonLayout's width exceeds 500px
                new FormLayout.ResponsiveStep("500px", 2));
        // Stretch the username field over 2 columns
        formLayout.setColspan(description, 2);

        setHeaderTitle("Add a new event");
        add(formLayout);

        getFooter().removeAll();
        getFooter().add(save);
        /*VerticalLayout buttonLayout = new VerticalLayout();
        buttonLayout.setAlignItems(FlexComponent.Alignment.END);
        buttonLayout.add(save);
        add(buttonLayout);*/
    }

    private void saveEvent() {
        if (binder.validate().hasErrors()) {
            return;
        }
        Event event = binder.getBean();
        event = eventService.save(event);
        onSave.accept(event);

        close();
    }
}
