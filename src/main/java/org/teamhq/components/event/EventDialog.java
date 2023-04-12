package org.teamhq.components.event;

import org.teamhq.data.entity.Event;
import org.teamhq.data.repository.EventRepository;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.function.SerializableConsumer;

public class EventDialog extends Dialog {

    private TextField eventName;

    private TextArea description;

    private DateTimePicker start;

    private DateTimePicker end;

    private Button save;

    private Binder<Event> binder;

    private SerializableConsumer<Event> onSave;

    private EventRepository eventRepository;

    public EventDialog(EventRepository eventRepository,
                       SerializableConsumer<Event> onSave) {
        this.eventRepository = eventRepository;
        this.onSave = onSave;
        eventName = new TextField("Event name");
        description = new TextArea("Event Description");
        start = new DateTimePicker("Event Start time");
        end = new DateTimePicker("Event End time");
        save = new Button("Save", click -> saveEvent());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Event event = new Event();

        binder = new Binder<>();
        binder.setBean(event);

        binder.forField(eventName).asRequired("Event name is mandatory")
                .bind(Event::getName, Event::setName);

        binder.forField(description).asRequired("Event Description is mandatory")
                .bind(Event::getDescription, Event::setDescription);

        binder.forField(start).asRequired("Start time is mandatory")
                .bind(Event::getStartDateTime, Event::setStartDateTime);

        binder.forField(end).asRequired("End time is mandatory")
                .bind(Event::getEndDateTime, Event::setEndDateTime);

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

        VerticalLayout buttonLayout = new VerticalLayout();
        buttonLayout.setAlignItems(FlexComponent.Alignment.END);
        buttonLayout.add(save);
        add(buttonLayout);
    }

    private void saveEvent() {
        if (binder.validate().hasErrors()) {
            return;
        }
        Event event = binder.getBean();
        eventRepository.saveAndFlush(event);
        onSave.accept(event);

        close();
    }
}
