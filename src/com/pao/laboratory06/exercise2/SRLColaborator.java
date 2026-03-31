package com.pao.laboratory06.exercise2;

import java.util.Scanner;

public class SRLColaborator extends Colaborator {
    private double cheltuieliLunare;

    public SRLColaborator() {setTipColaborator(TipColaborator.SRL);}
    SRLColaborator(String nume, String prenume, double venitBrutLunar, double cheltuieliLunare) {
        super(nume, prenume, venitBrutLunar, TipColaborator.SRL);
        this.cheltuieliLunare = cheltuieliLunare;
    }

    @Override
    double calculeazaVenitAnual() {
        return (getVenitBrutLunar() - cheltuieliLunare) * 12 * 0.84;
    }

    @Override
    public void citeste(Scanner in) {
        String nume = in.next();
        String prenume = in.next();
        double venitBrutLunar = in.nextDouble();
        double cheltuieliLunare = in.nextDouble();
        setNume(nume);
        setPrenume(prenume);
        setVenitBrutLunar(venitBrutLunar);
        this.cheltuieliLunare = cheltuieliLunare;
    }
}
