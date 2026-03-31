package com.pao.laboratory06.exercise2;



public abstract class Colaborator implements IOperatiiCitireScriere {
    private String nume, prenume;
    private double venitBrutLunar;
    private TipColaborator tipColaborator;
    Colaborator() {}
    Colaborator(String nume, String prenume, double venitBrutLunar, TipColaborator tipColaborator) {
        this.nume = nume;
        this.prenume = prenume;
        this.venitBrutLunar = venitBrutLunar;
        this.tipColaborator = tipColaborator;
    }

    public String getNume() {
        return nume;
    }
    public String getPrenume() {
        return prenume;
    }
    public double getVenitBrutLunar() {
        return venitBrutLunar;
    }
    public Persoana getTipPersoana() {
        return tipColaborator.getPersoana();
    }

    public void setNume(String nume) {
        this.nume = nume;
    }
    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }
    public void setVenitBrutLunar(double venitBrutLunar) {
        this.venitBrutLunar = venitBrutLunar;
    }
    public void setTipColaborator(TipColaborator tipColaborator) {
        this.tipColaborator = tipColaborator;
    }
    abstract double calculeazaVenitAnual();
    @Override
    public String tipContract() {
        return tipColaborator.name();
    }
    @Override
    public void afiseaza() {
        System.out.print(tipContract() + ": " + getNume() + " " + getPrenume() + ", venit net anual: " + calculeazaVenitAnual());
    }
};


