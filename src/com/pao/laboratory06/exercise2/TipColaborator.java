package com.pao.laboratory06.exercise2;

public enum TipColaborator {
    CIM(Persoana.Fizica),
    PFA(Persoana.Fizica),
    SRL(Persoana.Juridica);

    private Persoana tipPersoana;

    TipColaborator(Persoana tipPersoana) {
        this.tipPersoana = tipPersoana;
    }

    public Persoana getPersoana() {
        return tipPersoana;
    }
}
