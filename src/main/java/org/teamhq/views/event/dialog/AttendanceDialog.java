package org.teamhq.views.event.dialog;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.function.SerializableConsumer;
import org.teamhq.data.entity.Meal;
import org.teamhq.data.entity.MealChoice;
import org.teamhq.data.entity.RsvpAnswer;
import org.teamhq.data.entity.Vendor;
import org.teamhq.data.service.MealChoiceService;
import org.teamhq.data.service.MealService;
import org.teamhq.security.AuthenticatedUser;
import org.vaadin.addons.taefi.component.ToggleButtonGroup;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class AttendanceDialog extends Dialog {

    private final AuthenticatedUser authenticatedUser;
    private final MealChoiceService mealChoiceService;
    private final MealService mealService;
    private final SerializableConsumer<MealChoice> onSave;
    private Binder.Binding<AtomicReference<Vendor>, Vendor> vendorBinding;
    private final Binder<AtomicReference<Vendor>> vendorBinder = new Binder<>();
    private final AtomicReference<Vendor> selectedVendor = new AtomicReference<>();
    private final Binder<AtomicReference<RsvpAnswer>> rsvpBinder = new Binder<>();
    private final AtomicReference<RsvpAnswer> rsvpAnswer = new AtomicReference<>();
    private Meal meal;
    private final TextArea commentTextArea = new TextArea("Any special consideration?");
    private final VerticalLayout layout = new VerticalLayout();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MMM.yyyy HH:mm");

    public AttendanceDialog(AuthenticatedUser authenticatedUser,
                            MealChoiceService mealChoiceService,
                            MealService mealService,
                            SerializableConsumer<MealChoice> onSave) {
        this.authenticatedUser = authenticatedUser;
        this.mealChoiceService = mealChoiceService;
        this.mealService = mealService;
        this.onSave = onSave;
    }

    private void initUI() {
        setHeaderTitle(meal.getName());
        layout.removeAll();
        Div mealDesc = new Div();
        mealDesc.setText(meal.getDescription());
        layout.add(mealDesc);
        boolean singleTreatment = meal.getVendors().size() == 1; // otherwise, it is bigger than 1

        if (singleTreatment) {
            Label treatmentTitle = new Label("The treatment:");
            UnorderedList treatment = new UnorderedList(new ListItem(meal.getVendors().iterator().next().getName()));
            layout.add(treatmentTitle, treatment);
        } else {
            RadioButtonGroup<Vendor> treatmentOptions = new RadioButtonGroup<>("Available treatments:", meal.getVendors().stream().toList());
            treatmentOptions.setItemLabelGenerator(Vendor::getName);
            vendorBinding = vendorBinder.forField(treatmentOptions).asRequired("You should select one of the treatments").bind(AtomicReference::get, AtomicReference::set);
            vendorBinder.setBean(selectedVendor);
            layout.add(treatmentOptions);
        }

        ToggleButtonGroup<RsvpAnswer> answerGroup = new ToggleButtonGroup<>("Are you joining?");
        answerGroup.setWidth("270px");
        answerGroup.setMinWidth("270px");
        answerGroup.setItems(List.of(RsvpAnswer.values()));
        answerGroup.setItemLabelGenerator(RsvpAnswer::getLabel);
        answerGroup.setToggleable(false);
        answerGroup.setSelectedItemClassNameGenerator(answer -> "toggle-group-answer-" + answer.name().toLowerCase());
        rsvpBinder.forField(answerGroup).asRequired("Please determine your presence at this meal").bind(AtomicReference::get, AtomicReference::set);
        rsvpBinder.setBean(rsvpAnswer);
        layout.add(answerGroup);

        commentTextArea.setVisible(RsvpAnswer.YES.equals(rsvpAnswer.get()));
        if (commentTextArea.getValue() == null || commentTextArea.getEmptyValue().equals(commentTextArea.getValue().trim())) {
            commentTextArea.setValue(Optional.ofNullable(authenticatedUser.require().getComment()).orElse(""));
        }
        layout.add(commentTextArea);

        answerGroup.addValueChangeListener(event -> {
            commentTextArea.setVisible(RsvpAnswer.YES.equals(event.getValue()));
            if (vendorBinding != null) {
                vendorBinding.setAsRequiredEnabled(RsvpAnswer.YES.equals(event.getValue()));
                vendorBinder.refreshFields();
            }
        });


        Label latestPossibleRsvpMessage = new Label("Please don't forget to RSVP before:");
        latestPossibleRsvpMessage.getStyle().set("font-style", "italic");
        Label latestPossibleRsvpTime = new Label(formatter.format(meal.getFreezeDateTime()));
        latestPossibleRsvpTime.getStyle().set("font-weight", "bold");

        layout.add(new HorizontalLayout(latestPossibleRsvpMessage, latestPossibleRsvpTime));

        layout.setSizeFull();
        add(layout);

        Button submitButton = new Button("Submit", this::submitHandler);
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().removeAll();
        getFooter().add(submitButton);
    }

    public void open(Meal meal) {
        this.meal = meal;
        selectedVendor.set(null);
        rsvpAnswer.set(null);
        Optional.ofNullable(mealChoiceService.getMealChoiceByMealAndUser(meal, authenticatedUser.require()))
                .ifPresent(mealChoice -> {
                    this.selectedVendor.set(mealChoice.getVendor());
                    this.rsvpAnswer.set(mealChoice.getAnswer());
                    this.commentTextArea.setValue(mealChoice.getComment());
                });
        initUI();
        open();
    }

    private void submitHandler(ClickEvent<Button> clickEvent) {
        vendorBinder.validate();
        rsvpBinder.validate();
        if ((vendorBinding != null && vendorBinding.isAsRequiredEnabled() && selectedVendor.get() == null)|| rsvpAnswer.get() == null) {
            return;
        }
        saveMealChoice();
        close();
    }

    private void saveMealChoice() {
        MealChoice mealChoice = mealChoiceService.getMealChoiceByMealAndUser(this.meal, authenticatedUser.require());
        if (mealChoice == null) {
            mealChoice = new MealChoice();
        }
        mealChoice.setMeal(this.meal);
        mealChoice.setUser(authenticatedUser.require());
        mealChoice.setVendor(selectedVendor.get());
        mealChoice.setComment(commentTextArea.getValue());
        mealChoice.setAnswer(rsvpAnswer.get());

        mealChoice = mealChoiceService.save(mealChoice);
        meal = mealService.getById(meal.getId());
        onSave.accept(mealChoice);
    }


}
