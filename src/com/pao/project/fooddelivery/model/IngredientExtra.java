package com.pao.project.fooddelivery.model;

import java.util.Objects;

public class IngredientExtra {
    private Ingredient ingredient;
    private double pret;

    public IngredientExtra(Ingredient ingredient, double pret) {
        if (ingredient == null) {
            throw new IllegalArgumentException("Ingredientul extra nu poate fi vid!");
        }
        if (pret < 0) {
            throw new IllegalArgumentException("Pretul ingredientului extra nu poate fi negativ!");
        }
        this.ingredient = ingredient;
        this.pret = pret;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public double getPret() {
        return pret;
    }

    @Override
    public String toString() {
        return "IngredientExtra{ingredient=" + ingredient + ", pret=" + pret + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        IngredientExtra ie = (IngredientExtra) o;
        return Double.compare(pret, ie.pret) == 0 && Objects.equals(ingredient, ie.ingredient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredient, pret);
    }
}
