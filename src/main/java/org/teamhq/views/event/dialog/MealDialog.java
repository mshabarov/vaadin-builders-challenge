package org.teamhq.views.event.dialog;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.function.SerializableConsumer;

import org.teamhq.data.entity.Event;
import org.teamhq.data.entity.Meal;
import org.teamhq.data.entity.Vendor;
import org.teamhq.data.repository.MealRepository;
import org.teamhq.data.repository.VendorRepository;
import org.teamhq.data.service.MealService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MealDialog extends Dialog {

    private final VendorRepository vendorRepository;

    private final MealService mealService;

    private final MealUpdateMode mealUpdateMode;

    private LocalDate mealDate;

    private TimePicker startField;

    private TimePicker endField;

    private TextField nameField;

    private TextArea descriptionField;

    private DateTimePicker freezeDateTimeField;

    private MultiSelectComboBox<Vendor> vendorChoicesField;

    private Binder<Meal> mealBinder;

    private Button saveMealButton;

    private SerializableConsumer<Meal> onSave;


    public MealDialog(VendorRepository vendorRepository,
                      MealService mealService, LocalDate mealDate,
                      Meal meal, SerializableConsumer<Meal> onSave) {
        this.vendorRepository = vendorRepository;
        this.mealService = mealService;
        this.mealDate = mealDate;
        mealUpdateMode = MealUpdateMode.EDIT;
        this.onSave = onSave;
        initMealDialog(meal);
    }

    public MealDialog(VendorRepository vendorRepository,
                      MealService mealService, Event event,
                      LocalDate mealDate, SerializableConsumer<Meal> onSave) {
        this.vendorRepository = vendorRepository;
        this.mealService = mealService;
        this.mealDate = mealDate;
        mealUpdateMode = MealUpdateMode.CREATE;
        this.onSave = onSave;
        Meal meal = createDefaultMeal(event, mealDate);
        initMealDialog(meal);
    }

    private Meal createDefaultMeal(Event event, LocalDate mealDate) {
        Meal meal = new Meal();
        meal.setStartTime(LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT));
        meal.setEndTime(LocalDateTime.now());
        meal.setFreezeDateTime(mealDate.minusDays(1).atTime(17, 0));
        meal.setEvent(event);
        return meal;
    }

    private void initMealDialog(Meal meal) {
        initFields(meal);
        prepareLayout();
    }

    private void prepareLayout() {
        VerticalLayout timeLayout = new VerticalLayout(startField, endField);
        timeLayout.setPadding(false);
        timeLayout.setMargin(false);

        VerticalLayout informationLayout = new VerticalLayout(nameField, descriptionField);
        informationLayout.setPadding(false);
        informationLayout.setMargin(false);

        HorizontalLayout basicInformationLayout = new HorizontalLayout(timeLayout, informationLayout);
        basicInformationLayout.setPadding(false);
        basicInformationLayout.setMargin(false);

        VerticalLayout fieldsLayout = new VerticalLayout(basicInformationLayout, freezeDateTimeField, vendorChoicesField);
        fieldsLayout.setPadding(false);
        fieldsLayout.setMargin(false);

        add(fieldsLayout);

        setHeaderTitle(MealUpdateMode.CREATE.equals(mealUpdateMode) ? "New meal" : "Edit meal");
        getFooter().add(saveMealButton);
    }

    private void initFields(Meal meal) {
        mealBinder = new Binder<>();
        mealBinder.setBean(meal);

        startField = new TimePicker("Start time");
        mealBinder.forField(startField).asRequired("Start time is mandatory").bind(m -> m.getStartTime().toLocalTime(), (m, f) -> m.setStartTime(LocalDateTime.of(mealDate, f)));

        endField = new TimePicker("End time");
        mealBinder.forField(endField).asRequired("End time is mandatory").withValidator(startField.getValue()::isBefore, "End time should be after start time").bind(m -> m.getEndTime().toLocalTime(), (m, f) -> m.setEndTime(LocalDateTime.of(mealDate, f)));

        nameField = new TextField("Name");
        mealBinder.forField(nameField).asRequired("Name is mandatory").bind(Meal::getName, Meal::setName);

        descriptionField = new TextArea("Description");
        mealBinder.forField(descriptionField).bind(Meal::getDescription, Meal::setDescription);

        freezeDateTimeField = new DateTimePicker("Latest possible RSVP time");
        mealBinder.forField(freezeDateTimeField).asRequired("Freeze date is " +
                                                            "mandatory").withValidator(mealDate.atTime(startField.getValue())::isAfter, "Freeze time should be before start time").bind(Meal::getFreezeDateTime, Meal::setFreezeDateTime);

        vendorChoicesField = new MultiSelectComboBox<>("Vendor options");
        vendorChoicesField.setSizeFull();
        vendorChoicesField.setItems(vendorRepository.findAll());
        vendorChoicesField.setAllowCustomValue(true);
        vendorChoicesField.setItemLabelGenerator(Vendor::getName);
        vendorChoicesField.addCustomValueSetListener(event -> {
            LinkedHashSet<Vendor> vendorChoices =new LinkedHashSet<>(vendorChoicesField.getValue());
            Vendor customVendor = new Vendor();
            customVendor.setName(event.getDetail());
            vendorChoices.add(customVendor);
            vendorChoicesField.setValue(vendorChoices);
        });
        vendorChoicesField.setValue(meal.getVendors());

        saveMealButton = new Button("Save meal", click -> saveMeal());
        saveMealButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    }

    public void saveMeal() {
        if (mealBinder.validate().hasErrors()) {
            return;
        }
        saveNewVendors();
        Meal meal = mealBinder.getBean();
        meal.setVendors(new HashSet<>(vendorChoicesField.getValue()));
        meal = mealService.save(meal);
        onSave.accept(meal);
        close();
    }

    private List<Vendor> saveNewVendors() {
        Set<Vendor> newVendors = new HashSet<>(vendorChoicesField.getValue());
        newVendors.removeAll(vendorRepository.findAll());
        return vendorRepository.saveAllAndFlush(newVendors);
    }

    private enum MealUpdateMode {
        CREATE, EDIT;
    }
}
