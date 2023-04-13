package org.teamhq.data.entity;

public enum RsvpAnswer {
    YES("Yes"), MAYBE("Maybe?"), NO("No");

    private final String label;

    RsvpAnswer(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}