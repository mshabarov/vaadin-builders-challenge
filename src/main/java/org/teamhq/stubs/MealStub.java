package org.teamhq.stubs;

import java.time.LocalDateTime;

import com.vaadin.flow.component.html.Div;

public class MealStub extends Div {
    private final LocalDateTime from;
    private final LocalDateTime to;

    public static final int HEIGHT = 50;

    public MealStub(LocalDateTime from, LocalDateTime to) {
        setText("Some Meal");
        this.from = from;
        this.to = to;
        addClassName("meal-component");
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public LocalDateTime getTo() {
        return to;
    }
}
