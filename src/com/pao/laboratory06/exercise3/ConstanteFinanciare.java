package com.pao.laboratory06.exercise3;

public enum ConstanteFinanciare {
    TVA(0.19),
    SALARIU_MINIM(4000),
    COTA_IMPOZIT(0.10);

    private double valoare;

    ConstanteFinanciare(double valoare) {
        this.valoare = valoare;
    }

    double getValoare() {
        return valoare;
    }
}
