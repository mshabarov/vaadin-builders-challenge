package org.teamhq.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

@Entity
public class MealVendor {
    @Id
    private Number id;

    @ManyToOne
    @NotNull
    private Meal meal;

    @ManyToOne
    @NotNull
    private Vendor vendor;

    public Number getId() {
        return id;
    }

    public void setId(Number id) {
        this.id = id;
    }

    public Meal getMeal() {
        return meal;
    }

    public void setMeal(Meal meal) {
        this.meal = meal;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }
}
