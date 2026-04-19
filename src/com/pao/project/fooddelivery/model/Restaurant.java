package com.pao.project.fooddelivery.model;

import com.pao.project.fooddelivery.exception.ProcentInvalidException;

import java.util.List;
import java.util.Objects;

public class Restaurant {
    private String nume;
    private Adresa adresa;
    private Meniu meniu;
    private boolean livrareGratuita;
    private double taxaLivrare; // 5 RON de exemplu
    private double procentComisionSofer; // 10% de exemplu

    public Restaurant(String nume, Adresa adresa, double taxaLivrare,
                      double procentComisionSofer, boolean livrareGratuita) {
        if (nume == null || nume.isBlank()) {
            throw new IllegalArgumentException("Numele restaurantului nu poate fi gol!");
        }
        if (adresa == null) {
            throw new IllegalArgumentException("Adresa restaurantului nu poate fi vida!");
        }
        if (taxaLivrare < 0) {
            throw new IllegalArgumentException("Taxa de livrare trebuie sa fie pozitiva!");
        }
        this.nume = nume.trim();
        this.adresa = adresa;
        this.taxaLivrare = taxaLivrare;
        setProcentComisionSofer(procentComisionSofer);
        this.livrareGratuita = livrareGratuita;
        this.meniu = new Meniu();
    }

    public String getNume() {
        return nume;
    }

    public Adresa getAdresa() {
        return adresa;
    }

    public double getTaxaLivrare() {
        return taxaLivrare;
    }

    public double getProcentComisionSofer() {
        return procentComisionSofer;
    }

    public Meniu getMeniu() {
        return meniu;
    }

    public List<Produs> getProduse() {
        return meniu.getProduse();
    }

    public void adaugaProdus(Produs p) {
        if (p == null){
            throw new IllegalArgumentException("Produsul nu poate fi vid!");
        }
        meniu.adaugaProdus(p);
    }

    public void stergeProdus(Produs p) {
        if (p == null) {
            throw new IllegalArgumentException("Produsul nu poate fi vid!");
        }
        meniu.stergeProdus(p);
    }

    public void setProcentComisionSofer(double procent) {
        if (procent < 0 || procent > 1) {
            throw new ProcentInvalidException(procent);
        }
        this.procentComisionSofer = procent;
    }

    public boolean areLivrareaGratuita() {
        return livrareGratuita;
    }

    public List<Produs> cautaProdus(String nume) {
        if (nume == null || nume.isBlank()) {
            throw new IllegalArgumentException("Nu se poate cauta un produs cu nume vid!");
        }
        return meniu.cautaDupaNume(nume);
    }

    public List<Produs> filtreazaCategorie(String categorie) {
        if (categorie == null || categorie.isBlank()) {
            throw new IllegalArgumentException("Nu se poate filtra dupa o categorie vida!");
        }
        return meniu.filtreazaDupaCategorie(categorie);
    }

    @Override
    public String toString() {
        return "Restaurant{nume='" + nume + "', adresa=" + adresa + ", taxaLivrare=" + taxaLivrare +
                ", procentComisionSofer=" + procentComisionSofer * 100 + ", livrareGratuita=" +
                (livrareGratuita ? "da" : "nu") + "}";
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
        Restaurant r = (Restaurant) o;
        return Objects.equals(nume, r.nume) && Objects.equals(adresa, r.adresa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nume, adresa);
    }
}
