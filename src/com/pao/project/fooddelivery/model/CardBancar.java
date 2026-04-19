package com.pao.project.fooddelivery.model;

import com.pao.project.fooddelivery.exception.DebitInvalidException;
import com.pao.project.fooddelivery.exception.SumaInvalidaException;

public class CardBancar {
    private String numar;
    private double balanta;

    public CardBancar(String numar) {
        if (numar == null || numar.isBlank()) {
            throw new IllegalArgumentException("Numarul cardului bancar nu ar trebui sa fie gol!");
        }
        this.numar = numar.trim();
        balanta = 0;
    }

    public String getNumar() {
        return numar;
    }

    public void topUp(double suma) {
        if (suma <= 0 ) {
            throw new SumaInvalidaException(suma);
        }
        balanta += suma;
    }

    public void debiteaza(double suma) {
        if (balanta - suma < 0 || suma <= 0) {
            throw new DebitInvalidException(balanta, suma);
        }
        balanta -= suma;
    }

    public double getBalanta() {
        return balanta;
    }

    @Override
    public String toString() {
        return "CardBancar{numar='" + numar + "', balanta=" + balanta + "}";
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
        CardBancar cb = (CardBancar) o;
        return numar.equals(cb.numar);
    }
    @Override
    public int hashCode() {
        return numar.hashCode();
    }
}
