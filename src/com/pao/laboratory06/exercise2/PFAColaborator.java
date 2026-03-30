package com.pao.laboratory06.exercise2;

import java.util.Scanner;

public class PFAColaborator extends Colaborator {
    private double cheltuieliLunare;

    public PFAColaborator() {}
    PFAColaborator(String nume, String prenume, double venitBrutLunar, double cheltuieliLunare) {
        super(nume, prenume, venitBrutLunar, TipColaborator.PFA);
        this.cheltuieliLunare = cheltuieliLunare;
    }

    @Override
    public double calculeazaVenitAnual() {
        double venitBrutLunar = getVenitBrutLunar();
        double salariuMinimBrutLunar = 4050;
        double salariuMinimBrutPeAn = salariuMinimBrutLunar * 12;
        double venitNet = (venitBrutLunar - cheltuieliLunare) * 12;
        double impozitPeVenit = venitNet * 0.10;
        double cass = 0;
        if (venitNet < 6 * salariuMinimBrutPeAn) {
            cass = (6*salariuMinimBrutLunar) * 0.10;
        }
        else if (venitNet >= 6 * salariuMinimBrutPeAn && venitNet <= 72 * salariuMinimBrutPeAn) {
            cass = venitNet * 0.10;
        }
        else {
            cass = (72*salariuMinimBrutLunar) * 0.10;
        }
        double cas = 0;
        if (venitNet < 12*salariuMinimBrutPeAn) {
            cas = 0;
        }
        else if (venitNet >= 12*salariuMinimBrutPeAn && venitNet <= 24*salariuMinimBrutPeAn) {
            cas = (12*salariuMinimBrutLunar)*0.25;
        }
        else {
            cas = (24*salariuMinimBrutLunar) * 0.25;
        }
        double venitNetAnual = venitNet - impozitPeVenit - cass - cas;
        return venitNetAnual;
    }
    @Override
    public void citeste(Scanner in) {
        String nume = in.nextLine();
        String prenume = in.nextLine();
        double venitBrutLunar = in.nextDouble();
        double cheltuieliLunare = in.nextDouble();
        setNume(nume);
        setPrenume(prenume);
        setVenitBrutLunar(venitBrutLunar);
        this.cheltuieliLunare = cheltuieliLunare;
    }
}
