package com.pao.project.fooddelivery.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Produs {
    private String nume, categorie;
    private double pret;
    private List<Ingredient> ingrediente;
    private List<IngredientExtra> ingredienteExtraDisponibile;
    private ValoriNutritionale valoriNutritionale;

    public Produs(String nume, String categorie, double pret, ValoriNutritionale valoriNutritionale) {
        if (nume == null || nume.isBlank()) {
            throw new IllegalArgumentException("Numele produsului nu poate fi gol!");
        }
        if (pret < 0) {
            throw new IllegalArgumentException("Pretul produsului trebuie sa fie pozitiv!");
        }
        if (valoriNutritionale == null) {
            throw new IllegalArgumentException("Produsul trebuie sa contina valori nutritionale!");
        }
        this.nume = nume.trim();
        setCategorie(categorie); // validarea categoriei se face in setter
        this.pret = pret;
        this.ingrediente = new ArrayList<Ingredient>();
        this.ingredienteExtraDisponibile = new ArrayList<IngredientExtra>();
        this.valoriNutritionale = valoriNutritionale;
    }

    public String getNume() {
        return nume;
    }

    public double getPret() {
        return pret;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        if (categorie == null || categorie.isBlank()) {
            throw new IllegalArgumentException("Categoria produsului nu poate fi goala!");
        }
        this.categorie = categorie.trim().toUpperCase();
    }

    public ValoriNutritionale getValoriNutritionale() {
        return valoriNutritionale;
    }

    public List<Ingredient> getIngrediente() {
        return List.copyOf(ingrediente);
    }

    public List<IngredientExtra> getIngredienteExtraDisponibile() {
        return List.copyOf(ingredienteExtraDisponibile);
    }

    public List<Ingredient> getIngredienteOptionale() {
        List<Ingredient> ingredienteOptionale = new ArrayList<Ingredient>();
        for (Ingredient i : ingrediente) {
            if (i.isOptional()) {
                ingredienteOptionale.add(i);
            }
        }
        return ingredienteOptionale;
    }

    public List<Ingredient> getIngredienteCuAlergeni() {
        List<Ingredient> ingredienteAlergeni = new ArrayList<Ingredient>();
        for (Ingredient i : ingrediente) {
            if (i.isAlergen()) {
                ingredienteAlergeni.add(i);
            }
        }
        return ingredienteAlergeni;
    }

    public boolean areAlergeni() {
        for (Ingredient i : ingrediente) {
            if (i.isAlergen()) {
                return true;
            }
        }
        return false;
    }

    public boolean estePersonalizabil() {
        /*
        for (Ingredient i : ingrediente) {
            if (i.isOptional()) {
                return true;
            }
        }
        return false;
         */
        return !getIngredienteOptionale().isEmpty() || !ingredienteExtraDisponibile.isEmpty();
    }

    public void adaugaIngredient(Ingredient i) {
        if (i == null) {
            throw new IllegalArgumentException("Ingredientul nu trebuie sa fie vid!");
        }
        ingrediente.add(i);
    }

    public void adaugaIngrediente(List<Ingredient> ingrediente) {
        if (ingrediente == null || ingrediente.isEmpty()) {
            throw new IllegalArgumentException("Lista de ingrediente de adaugat la produs nu trebuie sa fie vida!");
        }
        for (Ingredient i : ingrediente) {
            adaugaIngredient(i);
        }
    }

    public void adaugaIngredientExtraDisponibil(IngredientExtra ie) {
        if (ie == null) {
            throw new IllegalArgumentException("Ingredientul extra nu poate fi vid!");
        }
        if (ingredienteExtraDisponibile.contains(ie)) {
            throw new IllegalArgumentException("Ingredientul extra exista deja pentru produs!");
        }
        ingredienteExtraDisponibile.add(ie);
    }

    @Override
    public String toString() {
        return "Produs{nume='" + nume + "', categorie='" + categorie + "', pret=" + pret + ", ingrediente=" + ingrediente +
                ", ingredienteExtraDisponibile=" + ingredienteExtraDisponibile + ", personalizabil=" +
                (estePersonalizabil() ? "da" : "nu") + ", valoriNutritionale=" + valoriNutritionale + "}";
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
        Produs p = (Produs) o;
        return Double.compare(pret, p.pret) == 0 && Objects.equals(nume, p.nume) && Objects.equals(categorie, p.categorie);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nume, categorie, pret);
    }
}
