package com.pao.laboratory06.exercise2;

import com.pao.laboratory06.exercise1.Angajat;

import java.util.Scanner;

public class CIMColaborator extends Colaborator {
    private boolean bonus = false;
    public CIMColaborator() {}
    public CIMColaborator(String nume, String prenume, double venitBrutLunar) {
        super(nume, prenume, venitBrutLunar, TipColaborator.CIM);
    }

    @Override
    public double calculeazaVenitAnual() {
        double calcul = getVenitBrutLunar() * 12 * 0.55;
        if (this.bonus)
        {
            return calcul * 1.10;
        }
        return calcul;
    }

    @Override
    public void citeste(Scanner in) {
        String nume = in.next();
        String prenume = in.next();
        double venitBrutLunar = in.nextDouble();
        String areBonus = in.next();
        setNume(nume);
        setPrenume(prenume);
        setVenitBrutLunar(venitBrutLunar);
        if (areBonus.equals("DA")) {
            this.bonus = true;
        }
        else if (areBonus.equals("NU")) {
            this.bonus = false;
        }
    }

    @Override
    public boolean areBonus() {
        return bonus;
    }
};
