package com.pao.laboratory06.exercise3;

public class Angajat extends Persoana {
    private double salariu;
    public Angajat(String nume, String prenume, String telefon, double salariu) {
        super(nume,prenume,telefon);
        this.salariu = salariu;
    }
    public double getSalariu() {
        return salariu;
    }
}
