package com.pao.project.fooddelivery.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Objects;


public class ProdusPersonalizat {
    private Produs produs;
    private Set<Ingredient> ingredienteEliminate;
    private Map<IngredientExtra, Integer> ingredienteExtra;

    public ProdusPersonalizat(Produs produs) {
        if (produs == null) {
            throw new IllegalArgumentException("Produsul nu poate fi vid!");
        }
        this.produs = produs;
        this.ingredienteEliminate = new HashSet<Ingredient>();
        this.ingredienteExtra = new HashMap<IngredientExtra, Integer>();
    }

    public Produs getProdus() {
        return produs;
    }

    public Set<Ingredient> getIngredienteEliminate() {
        return Set.copyOf(ingredienteEliminate);
    }

    public Map<IngredientExtra, Integer> getIngredienteExtra() {
        return Map.copyOf(ingredienteExtra);
    }

    public void eliminaIngredientOptional(Ingredient i) {
        if (i == null) {
            throw new IllegalArgumentException("Ingredientul de eliminat nu poate fi vid!");
        }
        if (!i.isOptional()) {
            throw new IllegalArgumentException("Doar ingredientele optionale pot fi eliminate!");
        }
        List<Ingredient> ingredienteProdus = produs.getIngrediente();
        if (!ingredienteProdus.contains(i)) {
            throw new IllegalArgumentException("Ingredientul nu apartine produsului!");
        }
        ingredienteEliminate.add(i);
    }
    public void restaureazaIngredientOptional(Ingredient i) {
        if (i == null) {
            throw new IllegalArgumentException("Ingredientul nu poate fi vid!");
        }
        ingredienteEliminate.remove(i);
    }
    public void adaugaExtra(IngredientExtra ie, int cant) {
        if (ie == null) {
            throw new IllegalArgumentException("Ingredientul extra nu poate fi vid!");
        }
        if (cant <= 0) {
            throw new IllegalArgumentException("Cantitatea ingredientului de adaugat trebuie sa fie pozitiva!");
        }
        List<IngredientExtra> ieExtraDisp = produs.getIngredienteExtraDisponibile();
        if (!ieExtraDisp.contains(ie)) {
            throw new IllegalArgumentException("Ingredientul extra precizat nu este disponibil pentru acest produs!");
        }
        if (ingredienteExtra.containsKey(ie)) {
            ingredienteExtra.put(ie, ingredienteExtra.get(ie) + cant);
        }
        else {
            ingredienteExtra.put(ie, cant);
        }
    }

    public void stergeExtra(IngredientExtra ie) {
        if (ie == null) {
            throw new IllegalArgumentException("Ingredientul extra nu poate fi vid!");
        }
        ingredienteExtra.remove(ie);
    }

    public double calculeazaPretTotal() {
        double total = produs.getPret();
        for (Map.Entry<IngredientExtra, Integer> elem : ingredienteExtra.entrySet()) {
            IngredientExtra ie = elem.getKey();
            Integer cant = elem.getValue();
            total += ie.getPret() * cant;
        }
        return total;
    }

    @Override
    public String toString() {
        return "ProdusPersonalizat{produs=" + produs.getNume() + ", ingredienteEliminate=" + ingredienteEliminate +
                ", ingredienteExtra=" + ingredienteExtra + "}";
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
        ProdusPersonalizat pp = (ProdusPersonalizat) o;
        return Objects.equals(produs, pp.produs) && Objects.equals(ingredienteEliminate, pp.ingredienteEliminate) &&
                Objects.equals(ingredienteExtra, pp.ingredienteExtra);
    }

    @Override
    public int hashCode() {
        return Objects.hash(produs, ingredienteEliminate, ingredienteExtra);
    }
}
