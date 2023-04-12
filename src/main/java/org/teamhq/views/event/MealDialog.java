package org.teamhq.views.event;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import org.teamhq.data.entity.Event;
import org.teamhq.data.entity.Meal;
import org.teamhq.data.entity.MealVendor;
import org.teamhq.data.entity.Vendor;
import org.teamhq.data.repository.MealRepository;
import org.teamhq.data.repository.MealVendorRepository;
import org.teamhq.data.repository.VendorRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MealDialog extends Dialog {

    private VendorRepository vendorRepository;

    private MealRepository mealRepository;

    private MealVendorRepository mealVendorRepository;

    private MealUpdateMode mealUpdateMode;

    private LocalDate mealDate;

    private TimePicker startField;

    private TimePicker endField;

    private TextField nameField;

    private TextArea descriptionField;

    private DateTimePicker freezeDateTimeField;

    private MultiSelectComboBox<Vendor> vendorChoicesField;

    private Binder<Meal> mealBinder;

    private Button saveMealButton;


    public MealDialog(VendorRepository vendorRepository, MealRepository mealRepository, MealVendorRepository mealVendorRepository, LocalDate mealDate, Meal meal) {
        this.vendorRepository = vendorRepository;
        this.mealRepository = mealRepository;
        this.mealVendorRepository = mealVendorRepository;
        this.mealDate = mealDate;
        mealUpdateMode = MealUpdateMode.EDIT;
        initMealDialog(meal);
    }

    public MealDialog(VendorRepository vendorRepository, MealRepository mealRepository, MealVendorRepository mealVendorRepository, Event event, LocalDate mealDate) {
        this.vendorRepository = vendorRepository;
        this.mealRepository = mealRepository;
        this.mealVendorRepository = mealVendorRepository;
        this.mealDate = mealDate;
        mealUpdateMode = MealUpdateMode.CREATE;
        Meal meal = createDefaultMeal(event, mealDate);
        initMealDialog(meal);
    }

    private Meal createDefaultMeal(Event event, LocalDate mealDate) {
        Meal meal = new Meal();
        meal.setStartTime(LocalTime.of(10, 0));
        meal.setEndTime(LocalTime.of(11, 0));
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
        System.out.println("1: "+meal.getEndTime());

        startField = new TimePicker("Start time");
        mealBinder.forField(startField).asRequired("Start time is mandatory").bind(Meal::getStartTime, Meal::setStartTime);

        endField = new TimePicker("End time");
        mealBinder.forField(endField).asRequired("End time is mandatory").withValidator(startField.getValue()::isBefore, "End time should be after start time").bind(Meal::getEndTime, Meal::setEndTime);

        nameField = new TextField("Name");
        mealBinder.forField(nameField).asRequired("Name is mandatory").bind(Meal::getName, Meal::setName);

        descriptionField = new TextArea("Description");
        mealBinder.forField(descriptionField).bind(Meal::getDescription, Meal::setDescription);

        freezeDateTimeField = new DateTimePicker("Active until");
        mealBinder.forField(freezeDateTimeField).asRequired("Freeze date is mandatory").withValidator(mealDate.atTime(startField.getValue())::isAfter, "Freeze time should be before start time").bind(Meal::getFreezeDateTime, Meal::setFreezeDateTime);

        vendorChoicesField = new MultiSelectComboBox<>("Vendor options");
        vendorChoicesField.setSizeFull();
        vendorChoicesField.setItems(getAllVendors());
        vendorChoicesField.setAllowCustomValue(true);
        vendorChoicesField.setItemLabelGenerator(Vendor::getName);
        vendorChoicesField.addCustomValueSetListener(event -> {
            LinkedHashSet<Vendor> vendorChoices =new LinkedHashSet<>(vendorChoicesField.getValue());
            Vendor customVendor = new Vendor();
            customVendor.setName(event.getDetail());
            vendorChoices.add(customVendor);
            vendorChoicesField.setValue(vendorChoices);
        });
        
        saveMealButton = new Button("Save meal", click -> saveMeal());
    }

    private Set<Vendor> getAllVendors() {
        // TODO implement
        Vendor v1 = new Vendor();
        v1.setName("Vendor 1");
        Vendor v2 = new Vendor();
        v2.setName("Vendor 2");
        Vendor v3 = new Vendor();
        v3.setName("Vendor 3");
        return Set.of(v1, v2, v3);
    }

    public void saveMeal() {
        if (mealBinder.validate().hasErrors()) {
            return;
        }
        saveNewVendors();
        Meal meal = mealBinder.getBean();
        mealRepository.saveAndFlush(meal);
        saveNewMealVendors(meal);
        close();
    }

    private void saveNewVendors() {
        Set<Vendor> newVendors = new HashSet<>(vendorChoicesField.getValue());
        newVendors.removeAll(getAllVendors());
        vendorRepository.saveAllAndFlush(newVendors);
    }

    private void saveNewMealVendors(Meal meal) {
        Set<MealVendor> mealVendors = vendorChoicesField.getValue().stream().map(vendor -> createMealVendor(vendor, meal)).collect(Collectors.toSet());
        mealVendorRepository.saveAll(mealVendors);

    }

    private MealVendor createMealVendor(Vendor vendor, Meal meal) {
        MealVendor mealVendor = new MealVendor();
        mealVendor.setVendor(vendor);
        mealVendor.setMeal(meal);
        return mealVendor;
    }

    private enum MealUpdateMode {
        CREATE, EDIT;
    }
}
