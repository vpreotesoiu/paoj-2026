package com.pao.project.fooddelivery.model;

import com.pao.project.fooddelivery.exception.ProdusInvalidException;
import com.pao.project.fooddelivery.exception.ProdusNegasitException;

import java.util.ArrayList;
import java.util.List;

public class Meniu {
    private List<Produs> produse;

    public Meniu() {
        this.produse = new ArrayList<Produs>();
    }

    public List<Produs> getProduse() {
        return List.copyOf(produse);
    }

    public void adaugaProdus(Produs p) {
        if (p == null) {
            throw new ProdusInvalidException(p);
        }
        if (produse.contains(p)) {
            throw new IllegalArgumentException("Produsul deja exista in meniu!");
        }
        produse.add(p);
    }

    public void stergeProdus(Produs p) {
        if (!produse.remove(p)) {
            throw new ProdusNegasitException(p);
        }
    }

    public List<Produs> cautaDupaNume(String nume) {
        if (nume == null || nume.isBlank()) {
            throw new IllegalArgumentException("Numele dupa care se cauta produsele in meniu nu poate fi vid!");
        }
        List<Produs> produseFiltrate = new ArrayList<Produs>();
        String numeCautat = nume.trim();
        for (Produs p : produse) {
            if (p.getNume().equalsIgnoreCase(numeCautat)) {
                produseFiltrate.add(p);
            }
        }
        return produseFiltrate;
    }

    public List<Produs> filtreazaDupaCategorie(String categorie) {
        if (categorie == null || categorie.isBlank()) {
            throw new IllegalArgumentException("Categoria de filtrare nu poate fi vida!!");
        }
        String catNorm = categorie.trim().toUpperCase();
        List<Produs> produseFiltrate = new ArrayList<Produs>();
        for (Produs p : produse) {
            if (p.getCategorie().equals(catNorm)) {
                produseFiltrate.add(p);
            }
        }
        return produseFiltrate;
    }

    @Override
    public String toString() {
        return "Meniu{produse=" + produse + "}";
    }
}
