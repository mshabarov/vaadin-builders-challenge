package org.teamhq.data.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

@Entity
public class MealChoice {
    @EmbeddedId
    @NotNull
    private MealChoiceId id;

    private String comment;

    public MealChoiceId getId() {
        return id;
    }

    public void setId(MealChoiceId id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Embeddable
    public static class MealChoiceId implements Serializable {
        @ManyToOne
        private User user;

        @ManyToOne
        private MealVendor mealVendor;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public MealVendor getMealVendor() {
            return mealVendor;
        }

        public void setMealVendor(MealVendor mealVendor) {
            this.mealVendor = mealVendor;
        }
    }
}
