package com.pao.project.fooddelivery.model;

public record Ingredient(String nume, boolean alergen, boolean optional) {

    public boolean isAlergen() {
        return alergen;
    }

    public boolean isOptional() {
        return optional;
    }

    @Override
    public String toString() {
        return "Ingredient{nume='" + nume + "', alergen='" + (alergen ? "da" : "nu") + "', optional='" + (optional ? "da" : "nu") + "'}";
    }
}
